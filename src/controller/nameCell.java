package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;

import javafx.scene.control.TreeCell;

public class nameCell extends TreeCell<Path> {
	@Override
	public void updateItem(Path file, boolean empty) {
		super.updateItem(file, empty);
		if(!empty) {
			String fullPath = file.toString();
			if(!fullPath.endsWith(File.separator)) {
				String value = file.toString();
				int indexOf = value.lastIndexOf(File.separator);
				if(indexOf>0)
					value = value.substring(indexOf+1);
				DosFileAttributes attr = null;
				try {
					attr = Files.readAttributes(file, DosFileAttributes.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//rahs
				value+= " ";
				
				if(attr.isReadOnly())
					value+="r";
				else
					value+="-";
				if(attr.isArchive())
					value+="a";
				else
					value+="-";
				if(attr.isHidden())
					value+="h";
				else
					value+="-";
				if(attr.isSystem())
					value+="s";
				else
					value+="-";
				setText(value);
			}
		}
	}
}
