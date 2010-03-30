package view;

import util.FileObject;
import core.FileTransfer;
import blunet.*;

public class LocalFileOverwriteConfirm extends Confirm {
	
	FileObject fo;
	FileTransfer model;
	
	public LocalFileOverwriteConfirm(blunet midlet,
			FileTransfer model, FileObject fo,
			String text, String okText, String cancelText) {
		super(midlet, text, okText, cancelText);
		this.model = model;
		this.fo = fo;
	}
	
	public void okAction() {
		fo.setOverwritable(true);
		//UIFsm fsm = midlet.getUIFsm();
		//fsm.inEvent(UIEvent.ENTER_BT_DOWNLOAD_FILE_PROGRESS);
	}
	
	public void cancelAction() {
		model.remove(fo);
		//UIFsm fsm = midlet.getUIFsm();
		//fsm.inEvent(UIEvent.ENTER_BT_DOWNLOAD_FILE_PROGRESS);
	}
}
