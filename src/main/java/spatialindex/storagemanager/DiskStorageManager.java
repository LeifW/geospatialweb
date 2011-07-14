// Spatial Index Library
//
// Copyright (C) 2002  Navel Ltd.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// Contact information:
//  Mailing address:
//    Marios Hadjieleftheriou
//    University of California, Riverside
//    Department of Computer Science
//    Surge Building, Room 310
//    Riverside, CA 92521
//
//  Email:
//    marioh@cs.ucr.edu

package spatialindex.storagemanager;

import java.util.*;
import java.io.*;

public class DiskStorageManager implements IStorageManager {
	private RandomAccessFile m_dataFile = null;
	private RandomAccessFile m_indexFile = null;
	private int m_pageSize = 0;
	private int m_nextPage = -1;
	private TreeSet<Integer> m_emptyPages = new TreeSet<Integer>();
	private HashMap<Integer, Entry> m_pageIndex = new HashMap<Integer, Entry>();
	private byte[] m_buffer = null;

	public DiskStorageManager(String name, int pageSize)
			throws SecurityException, NullPointerException, IOException,
			FileNotFoundException, IllegalArgumentException {
		if (name == null)
			throw new IllegalArgumentException(
					"Property FileName was not specified.");
		initFiles(name, false);
		m_pageSize = pageSize;
		m_nextPage = 0;
		m_buffer = new byte[m_pageSize];
	}

	public DiskStorageManager(String name) throws SecurityException,
			NullPointerException, IOException, FileNotFoundException,
			IllegalArgumentException {
		if (name == null)
			throw new IllegalArgumentException(
					"Property FileName was not specified.");
		initFiles(name, true);

		try {
			m_pageSize = m_indexFile.readInt();
		} catch (EOFException ex) {
			throw new IllegalStateException("Failed reading pageSize.");
		}

		try {
			m_nextPage = m_indexFile.readInt();
		} catch (EOFException ex) {
			throw new IllegalStateException("Failed reading nextPage.");
		}

		// create buffer.
		m_buffer = new byte[m_pageSize];

		try {
			int count = m_indexFile.readInt();

			// load empty pages in memory
			for (int cCount = 0; cCount < count; cCount++)
				m_emptyPages.add(m_indexFile.readInt());

			// load index table in memory.
			count = m_indexFile.readInt();
			for (int cCount = 0; cCount < count; cCount++) {
				Entry e = new Entry();
				int id = m_indexFile.readInt();
				e.m_length = m_indexFile.readInt();

				int count2 = m_indexFile.readInt();

				for (int cCount2 = 0; cCount2 < count2; cCount2++) {
					e.m_pages.add(m_indexFile.readInt());
				}
				m_pageIndex.put(id, e);
			}
		} catch (EOFException ex) {
			throw new IllegalStateException("Corrupted index file.");
		}

	}

	private void initFiles(String name, boolean reuse) throws IOException {
		File indexFile = new File(name + ".idx");
		File dataFile = new File(name + ".dat");
		if (!reuse) {
			if (indexFile.exists())
				indexFile.delete();
			if (dataFile.exists())
				dataFile.delete();
			if (!indexFile.createNewFile())
				throw new IOException("Index file cannot be opened: "
						+ indexFile.getAbsolutePath());
			if (!dataFile.createNewFile())
				throw new IOException("Data file cannot be opened: "
						+ dataFile.getAbsolutePath());
		}
		m_indexFile = new RandomAccessFile(indexFile, "rw");
		m_dataFile = new RandomAccessFile(dataFile, "rw");
	}

	public int entries() {
		return m_pageIndex.size();
	}

	public void flush() {
		try {
			m_indexFile.seek(0l);
			m_indexFile.writeInt(m_pageSize);
			m_indexFile.writeInt(m_nextPage);
			m_indexFile.writeInt(m_emptyPages.size());

			for (int p : m_emptyPages)
				m_indexFile.writeInt(p);

			m_indexFile.writeInt(m_pageIndex.size());

			for (Map.Entry<Integer, Entry> me : m_pageIndex.entrySet()) {
				m_indexFile.writeInt(me.getKey());
				Entry e = me.getValue();
				m_indexFile.writeInt(e.m_length);
				m_indexFile.writeInt(e.m_pages.size());
				for (int page : e.m_pages)
					m_indexFile.writeInt(page);
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Corrupted index file.");
		}
	}

	public byte[] loadByteArray(final int id) {
		if (!m_pageIndex.containsKey(id))
			throw new InvalidPageException(id);
		return loadByteArray(m_pageIndex.get(id));
	}

	private byte[] loadByteArray(Entry e) {
		byte[] data = new byte[e.m_length];
		int cIndex = 0;
		int cRem = e.m_length;

		for (int page : e.m_pages) {
			try {
				m_dataFile.seek(page * m_pageSize);
				if (m_dataFile.read(m_buffer) != m_pageSize)
					throw new IllegalStateException("Corrupted data file.");
			} catch (IOException ex) {
				throw new IllegalStateException("Corrupted data file.");
			}
			int cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
			System.arraycopy(m_buffer, 0, data, cIndex, cLen);
			cIndex += cLen;
			cRem -= cLen;
		}
		return data;
	}

	public int storeByteArray(final int id, final byte[] data) {
		if (id == NewPage) {
			Entry e = new Entry();
			e.m_length = data.length;

			int cIndex = 0;
			int cPage;
			int cRem = data.length;
			int cLen;

			while (cRem > 0) {
				if (!m_emptyPages.isEmpty()) {
					Integer i = (Integer) m_emptyPages.first();
					m_emptyPages.remove(i);
					cPage = i.intValue();
				} else {
					cPage = m_nextPage;
					m_nextPage++;
				}

				cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
				System.arraycopy(data, cIndex, m_buffer, 0, cLen);

				try {
					m_dataFile.seek(cPage * m_pageSize);
					m_dataFile.write(m_buffer);
				} catch (IOException ex) {
					throw new IllegalStateException("Corrupted data file.");
				}

				cIndex += cLen;
				cRem -= cLen;
				e.m_pages.add(new Integer(cPage));
			}

			int i = e.m_pages.get(0);
			m_pageIndex.put(e.m_pages.get(0), e);
			return i;
		} else {
			// find the entry.
			Entry oldEntry = m_pageIndex.get(id);
			if (oldEntry == null)
				throw new InvalidPageException(id);

			m_pageIndex.remove(new Integer(id));

			Entry e = new Entry();
			e.m_length = data.length;

			int cIndex = 0;
			int cPage;
			int cRem = data.length;
			int cLen, cNext = 0;

			while (cRem > 0) {
				if (cNext < oldEntry.m_pages.size()) {
					cPage = oldEntry.m_pages.get(cNext);
					cNext++;
				} else if (!m_emptyPages.isEmpty()) {
					int i = m_emptyPages.first();
					m_emptyPages.remove(i);
					cPage = i;
				} else {
					cPage = m_nextPage;
					m_nextPage++;
				}

				cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
				System.arraycopy(data, cIndex, m_buffer, 0, cLen);

				try {
					m_dataFile.seek(cPage * m_pageSize);
					m_dataFile.write(m_buffer);
				} catch (IOException ex) {
					throw new IllegalStateException("Corrupted data file.");
				}

				cIndex += cLen;
				cRem -= cLen;
				e.m_pages.add(new Integer(cPage));
			}

			while (cNext < oldEntry.m_pages.size()) {
				m_emptyPages.add(oldEntry.m_pages.get(cNext));
				cNext++;
			}

			int i = e.m_pages.get(0);
			m_pageIndex.put(i, e);
			return i;
		}
	}

	public void deleteByteArray(final int id) {
		// find the entry.
		Entry e = m_pageIndex.get(id);
		if (e == null)
			throw new InvalidPageException(id);
		m_pageIndex.remove(id);
		for (int cIndex = 0; cIndex < e.m_pages.size(); cIndex++)
			m_emptyPages.add(e.m_pages.get(cIndex));
	}

	public void close() {
		flush();
		try {
			m_dataFile.close();
		} catch (IOException e) {
		}
		try {
			m_indexFile.close();
		} catch (IOException e) {
		}
	}

	class Entry {
		int m_length = 0;
		ArrayList<Integer> m_pages = new ArrayList<Integer>();
		int page(int i) {
			return m_pages.get(i);
		}
	}
}
