package org.colston.kicks.document.persistence;

import java.io.InputStream;
import java.io.OutputStream;

import org.colston.kicks.document.KicksDocument;

public interface DocumentStore
{
	static DocumentStore create() throws Exception
	{
		XMLDocumentStore ds = new XMLDocumentStore();
		ds.initialise();
		return ds;
	}
	
	KicksDocument load(InputStream is) throws Exception;
	
	void save(KicksDocument doc, OutputStream os) throws Exception;
	
	KicksDocument clone(KicksDocument doc);
}
