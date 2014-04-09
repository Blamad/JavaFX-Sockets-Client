package controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

public class PathTreeItem extends TreeItem<Path>{

	private String fullPath;
	public String getFullPath() { return fullPath; }
	
	private boolean isDirectory;
	public boolean isDirectory() { return this.isDirectory; }
	
	private Path path;
	private Vector<PathTreeItem> childrenList = null;
	
	@SuppressWarnings("unchecked")
	public PathTreeItem(Path file) throws IOException {
		super(file);
		path = file;
		this.fullPath = file.toString();
		
		if(Files.isDirectory(file)) {
			this.isDirectory = true;
			childrenList = new Vector();
			wczytaj_katalog();
		}
		else
			this.isDirectory = false;
	
		
		this.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
			@Override
			public void handle(Event e) {
				PathTreeItem source = (PathTreeItem)e.getSource();
				
				try {
					if(source.getChildren().isEmpty()) {
						Path path = Paths.get(source.getFullPath());
						if(isDirectory) {
							DirectoryStream<Path> dir=Files.newDirectoryStream(path);
							for(Path file:dir) {
								PathTreeItem treeNode = new PathTreeItem(file);
								source.getChildren().add(treeNode);
							}
						}
					} else {
					}
				} catch(IOException x) {
					x.printStackTrace();
				}
			}
		});
	}
	
	private void wczytaj_katalog() throws IOException {	//jezeli mamy do czynienia z katalogiem
		
		DirectoryStream<Path> files = Files.newDirectoryStream(path);
		PathTreeItem tmp = null;
		for(Path it : files) {
			tmp = new PathTreeItem(it.toAbsolutePath());
			this.getChildren().add(tmp);
			childrenList.add(tmp);
		}
	}
	
	public void delete() {
		if(this.isLeaf()) {
			TreeItem<Path> parent = this.getParent();
			parent.getChildren().remove(this);
			
			//-------------- Kasowanie z dysku - na razie obejdzie sie bez tego
			try {
				Files.deleteIfExists(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			for(PathTreeItem it : childrenList)
				it.delete();
			delete();
		}
	}
	
	public void create(String name, boolean plik) {
		PathTreeItem tmp = null;
		String pathFile;
		Path newPath;
		if(isDirectory) { // jezeli wskazujemy na directory
			pathFile = this.getFullPath();
			pathFile += "\\" + name;
			newPath = Paths.get(pathFile);
			try {
				if(plik)
					Files.createFile(newPath);
				else
					Files.createDirectory(newPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				tmp = new PathTreeItem(newPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.getChildren().add(tmp);
			childrenList.add(tmp);
		} else {
			System.out.println("Wskazujesz na plik! Wska¿ folder w ktorym plik ma byæ stworzony.");
		}
	}
}