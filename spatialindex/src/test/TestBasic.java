package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import spatialindex.storagemanager.DiskStorageManager;
import spatialindex.storagemanager.IStorageManager;

public class TestBasic {

	@Test
	public void basic() throws SecurityException, NullPointerException, FileNotFoundException, IllegalArgumentException, IOException {
		DiskStorageManager disk =  new DiskStorageManager("foo", 1024);
		File a = new File("foo.dat");
		File b = new File("foo.idx");
		assertTrue(a.exists());
		assertTrue(b.exists());
		disk.close();
		a.delete();
		b.delete();
	}
	
	@Test
	public void basic2() throws SecurityException, NullPointerException, FileNotFoundException, IllegalArgumentException, IOException {
		boolean caught = false;
		try {
			new DiskStorageManager("bad/foo", 1024);
		} catch (IOException e) {
			caught = true;
		}
		assertTrue(caught);

	}
	
	@Test
	public void basic3() throws SecurityException, NullPointerException, FileNotFoundException, IllegalArgumentException, IOException {
		DiskStorageManager disk = new DiskStorageManager("foo", 16);
		File a = new File("foo.dat");
		File b = new File("foo.idx");
		assertTrue(a.exists());
		assertTrue(b.exists());
		int first = disk.storeByteArray(IStorageManager.NewPage, "first one".getBytes());
		int id=0;
		for(int i=0; i<100; i++) {
			id = disk.storeByteArray(IStorageManager.NewPage, ("for index " + i).getBytes());
		}
		assertEquals(disk.entries(), 101);
		byte[] entry = disk.loadByteArray(id);
		String result = new String(entry);
		assertEquals(result, "for index 99");
		result = new String(disk.loadByteArray(first));
		assertEquals(result, "first one");
		
		id = disk.storeByteArray(IStorageManager.NewPage, "1234567890abcdefghi".getBytes());
		
		for(int i=0; i<100; i++)
			disk.storeByteArray(IStorageManager.NewPage, ("filling" + i).getBytes());
		
		
		result = new String(disk.loadByteArray(id));
		assertEquals(result, "1234567890abcdefghi");
		disk.close();
		

		a.delete();
		b.delete();
	}
	
	
}
