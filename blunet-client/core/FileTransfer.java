package core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Gauge;
import javax.obex.ResponseCodes;

import local.Labels;
import local.Local;

import util.*;
import view.BWAlert;
import view.LocalFileOverwriteConfirm;
import view.ProgressScreen;
import blunet.*;

import comm.OBEXListener;
import comm.OBEXRequest;
import comm.OBEXResponse;
import comm.OBEXSession;

public class FileTransfer implements OBEXListener {

	ProgressScreen progressScreen;
	blunet midlet;
	String progressText;

	protected String btName;
	protected OBEXSession session;
	protected Stack downloadStack;

    Worker w;

	GetFile thread;
	protected boolean cancelled = false;

	public FileTransfer(blunet midlet,
			ProgressScreen progressScreen, String btName, OBEXSession session, String localFolder, Vector localFiles, String remoteFolder, Vector remoteFiles ) {
		this.midlet = midlet;
		this.progressScreen = progressScreen;
		downloadStack = new Stack(10);

        onEnter();
        onUpdate(btName, session, btName, remoteFiles, btName, localFiles);
        
	}

	public void progressUpdate(int part, int total) {
		String text = "";
		if (total > 0)
			text = "" + (part / 1024) + "K of " + (total / 1024) + "K copied";
		else
			text = "" + (part / 1024) + "K copied";
		progressScreen.progressUpdate(part, total, text);
	}

	public void progressCompleted(Object obj) {
	}

	public void remove(FileObject fo) {
		FilesForDownload ffd = (FilesForDownload) downloadStack.peek();
		FolderObject remoteFolder = ffd.getRemoteFolder();
		if (remoteFolder != null) {
			remoteFolder.removeFileObject(fo);
		}
	}

	public void onEnter() {
		if (downloadStack.size() == 0) {
			//UIFsm fsm = midlet.getUIFsm();
			//fsm.outEvent(UIEvent.ENTER_BT_DOWNLOAD_FILE);
		} else {
			midlet.changeScreen(progressScreen);
			progressScreen.setLabel(Local.get(Labels.DOWNLOADING) + " "
					+ Local.get(Labels.FILES) + " " + Local.get(Labels.FROM)
					+ " " + btName + " ...");
			progressScreen.setMaxValue(Gauge.INDEFINITE);
			progressScreen.setValue(Gauge.CONTINUOUS_RUNNING);
			session.setOBEXListener(this);
			thread = new GetFile(midlet, this);
			thread.start();
		}
	}

	public void onLeave() {
	}

	public void onRefresh() {
		onEnter();
	}

	public void onUpdate(String btName, OBEXSession session,
			String remoteFolder, Vector remoteFiles, String localFolder,
			Vector localFiles) {
		this.btName = btName;
		this.session = session;
		downloadStack.reset();
		push(remoteFolder, remoteFiles, localFolder, localFiles);
	}
//USE PUSH IN VIEW and THEN RUN() THE THREAD!!!, POP and try to connect
	public FilesForDownload push(String remoteFolder, Vector remoteFiles,
			String localFolder, Vector localFiles) {
		FilesForDownload ffd = new FilesForDownload();
		FolderObject remoteFolderObject = new FolderObject(remoteFolder,
				remoteFiles);
		FolderObject localFolderObject = new FolderObject(localFolder,
				localFiles);
		ffd.setLocalFolder(localFolderObject);
		ffd.setRemoteFolder(remoteFolderObject);
		downloadStack.push(ffd);
		return ffd;
	}

	public void onCancel() {
		if (thread != null) {
			thread.cancel();
			thread = null;
		}
		cancelled = true;
	}

}
class GetFile extends Thread {
	private blunet midlet;
	int connectionCode;
	FileTransfer progress;

	GetFile(blunet midlet, FileTransfer progress) {
		this.midlet = midlet;
		this.progress = progress;
	}

	public void run() {
		while (progress.downloadStack.size() > 0) {

			FilesForDownload ffd = (FilesForDownload) progress.downloadStack.peek();
			FolderObject remoteFolderObject = ffd.getRemoteFolder();
			FolderObject localFolderObject = ffd.getLocalFolder();
			Vector remoteFiles = remoteFolderObject.getFileObjectList();
			Vector localFiles = localFolderObject.getFileObjectList();
			String remoteFolder = remoteFolderObject.getFullPath();
			if (!remoteFolder.endsWith(FileObject.FILE_SEPARATOR))
				remoteFolder += FileObject.FILE_SEPARATOR;
			String localFolder = localFolderObject.getFullPath();

//			progress.progressScreen.setText("files to be downloaded: " + remoteFiles.size());
//			Kits.sleep(3000);

			while (!remoteFiles.isEmpty()) {

				FileObject fo = (FileObject) remoteFiles.elementAt(0);

				String remoteFile = fo.getName();
				progress.progressScreen.setLabel(Local.get(Labels.DOWNLOAD) + " "
						+ remoteFolder + remoteFile + " to " + localFolder);
				progress.progressScreen.setText("");

				if (fo.isFolder()) {
					remoteFiles.removeElementAt(0);
					String remoteChildFolder = remoteFolder + remoteFile;
					try {
						OBEXResponse resp = progress.session.getFolder(remoteFile);
						if (resp.getOpCode() == OBEXRequest.ABORT) {
							progress.progressScreen
									.setText("Download folder aborted: "
											+ resp.getRespCode());
							Kits.sleep(3000);
						}
						
						if (resp.getRespCode() == ResponseCodes.OBEX_HTTP_OK) {
							Vector remoteChildFiles = RemoteData.getInstance().getFileList(resp.getBody());
							// create local child folder for remote child folder
							FileConnection fc = null;
							Vector localChildFiles = new Vector();
							if (!remoteFile.endsWith(FileObject.FILE_SEPARATOR))
								remoteFile += FileObject.FILE_SEPARATOR;
							String localChildFolder = localFolder + remoteFile;
							fc = (FileConnection) Connector.open("file:///"
									+ localChildFolder, Connector.READ_WRITE);
							if (!fc.exists()) {
								fc.mkdir();
								// update local folder
								FileObject fn = new FileObject(remoteFile);
								fn.setFolder(true);
								localFiles.addElement(fn);
							}
							else {
								// load local files from local child folder
								Enumeration listOfFiles = fc.list();
								while (listOfFiles.hasMoreElements()) {
									String name = (String) listOfFiles
											.nextElement();
									localChildFiles
											.addElement(new FileObject(name));
								}
							}
							fc.close();

							ffd = progress.push(remoteChildFolder, remoteChildFiles,
									localChildFolder, localChildFiles);
							ffd.setChild(true);
							
//							progress.progressScreen.insertTextField("remote:"
//									+ remoteChildFolder + "("
//									+ remoteChildFiles.size() + ")," + "local:"
//									+ localChildFolder + "("
//									+ localChildFiles.size() + ")");
//							Kits.sleep(3000);
							
							remoteFolderObject = ffd.getRemoteFolder();
							localFolderObject = ffd.getLocalFolder();
							remoteFiles = remoteFolderObject.getFileObjectList();
							localFiles = localFolderObject.getFileObjectList();
							remoteFolder = remoteFolderObject.getFullPath();
							if (!remoteFolder.endsWith(FileObject.FILE_SEPARATOR))
								remoteFolder += FileObject.FILE_SEPARATOR;
							localFolder = localFolderObject.getFullPath();
						}
						else {
							progress.progressScreen
									.setText("Downalod folder failed: "+ resp.getRespCode());
							Kits.sleep(3000);
						}
					} catch (SecurityException e) {
						BWAlert.errorAlert("Security Exception: " + e.getMessage());
					} catch (IOException e) {
						BWAlert.errorAlert("IO Exception: " + e.getMessage());
					}
				}
				else {
					boolean isExist = exist(localFiles, remoteFile);
					if (isExist && !fo.isOverwritable()) {
						LocalFileOverwriteConfirm fileOverwriteConfirm = new LocalFileOverwriteConfirm(
								midlet, progress, fo, Local
										.get(Labels.OVERWRITE_LOCAL_FILE_CONFIRM)
										+ " " + remoteFile + "?", Local
										.get(Labels.YES), Local.get(Labels.NO));
						midlet.changeScreen(fileOverwriteConfirm);
						return;
					}

					// download file
					try {
						int size = fo.getSize();
						OBEXResponse resp = progress.session.getFile(remoteFile,
								size);
						if (resp.getOpCode() == OBEXRequest.ABORT) {
							progress.progressScreen
									.setText("Download file aborted: "
											+ resp.getRespCode());
							Kits.sleep(2000);
						}
						if (resp.getRespCode() == ResponseCodes.OBEX_HTTP_OK) {
							byte[] result = resp.getBody();
							String fpath = "file:///" + localFolder + remoteFile;
							// write into local directory
							FileConnection fc = null;
							try {
								fc = (FileConnection) Connector.open(fpath,
										Connector.READ_WRITE);
								if (!fc.exists())
									fc.create();
								else
									fc.truncate(0);
								// to make sure the file can be removed later
								fc.setHidden(false);
								fc.setReadable(true);
								fc.setWritable(true);
								OutputStream os = fc.openOutputStream();
								os.write(result);
								os.flush();
								os.close();
								fc.close();
								// update current folder
								if (!isExist && LocalFileHandler.getCurrentFolder().getFullPath().equals(localFolder))
									LocalFileHandler.getCurrentFolder()
											.addFileObject(
													new FileObject(remoteFile));
							} catch (SecurityException e) {
								progress.progressScreen
										.setText("Save file SecurityException: "
												+ e.getMessage());
								Kits.sleep(2000);
							} catch (IOException e) {
								progress.progressScreen
										.setText("Save file IOException: "
												+ e.getMessage());
								Kits.sleep(2000);
							}
						} else {
							progress.progressScreen
									.setText("Downalod file failed: "
											+ resp.getRespCode());
							Kits.sleep(2000);
						}

					} catch (IOException e) {
						progress.progressScreen.setText("Download IOException: "
								+ e.getMessage());
						Kits.sleep(3000);
					}
					// for download file, the call to remoteFiles.removeElementAt(0)
					// has to be made at the end of process: The file
					// overwriting checking has to leave the current loop and re-enter
					// into the loop after confirmation. Removing file from remoteFiles vector
					// earlier will lose the file after re-enter the loop
					remoteFiles.removeElementAt(0);
				}
			}
			
			if (ffd.isChild()) {
				try {
					progress.session.changeFolder("..");
				}
				catch (IOException ioe) {
					BWAlert.errorAlert("download(): " + ioe.toString());
				}
			}

			progress.downloadStack.pop();
		}
		
		//UIFsm fsm = midlet.getUIFsm();
		//fsm.inEvent(UIEvent.ENTER_BT_DOWNLOAD_FILE_PROGRESS);
	}

	void cancel() {
		progress.session.cancel();
	}

	boolean exist(Vector localFiles, String remoteFile) {
		for (int i = 0; i < localFiles.size(); i++) {
			FileObject fo = (FileObject) localFiles.elementAt(i);
			if (fo.getName().equals(remoteFile))
				return true;
		}
		return false;
	}

}
