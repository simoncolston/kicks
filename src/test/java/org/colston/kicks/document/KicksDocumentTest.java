package org.colston.kicks.document;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.colston.kicks.document.persistence.DocumentStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KicksDocumentTest
{
	static DocumentStore docStore;
	
	@BeforeAll
	static void setUp() throws Exception
	{
		docStore = DocumentStore.create();
	}

	@Test
	void testClone() throws Exception
	{
		KicksDocument[] docs = new KicksDocument[2];
		docs[0] = createMinimalDocument();
		docs[1] = createFullSingletonDocument();
		
		for (KicksDocument doc : docs)
		{
			KicksDocument doc2 = docStore.clone(doc);
			assertFalse(doc == doc2);
			assertEquals(doc, doc2);
		}
	}
	
	@Test
	void testSaveAndLoad() throws Exception
	{
		KicksDocument[] docs = new KicksDocument[2];
		docs[0] = createMinimalDocument();
		docs[1] = createFullSingletonDocument();
		
		for (KicksDocument doc : docs)
		{
			docStore.save(doc, System.out);
			System.out.println();
			
			Path tmp = Files.createTempFile("kicks", ".kicks");
			tmp.toFile().deleteOnExit();
//			System.out.println("Saved: " + tmp.toString());
			try (OutputStream os = Files.newOutputStream(tmp))
			{
				docStore.save(doc, os);
			}
			try (InputStream is = Files.newInputStream(tmp))
			{
				KicksDocument doc2 = docStore.load(is);
				assertNotNull(doc2);
			}
		}
	}

	private KicksDocument createFullSingletonDocument()
	{
		KicksDocument doc = new KicksDocument();
		doc.getProperties().setName("asadoya");
		doc.getProperties().setDescription("This is a song about a beautiful girl");
		doc.getProperties().setVersion("V1");
		
		Song song = new Song(0);
		doc.getSongs().add(song);
		
		song.setTitle("安里屋ユンタ");
		song.setTuning(Tuning.HONCHOUSHI);
		song.setTempo("100");
		
		Note note = new Note(0, 8, 1, 0);
		doc.getNotes().add(note);
		
		note.setSmall(true);
		note.setAccidental(Accidental.FLAT);
		note.setUtou(Utou.KAKI);
		note.setChord(true);
		note.setSlur(true);
		
		Break brk = new Break(15, BreakType.LINE);
		doc.getBreaks().add(brk);
		
		Repeat repeat = new Repeat(0, 4, true);
		doc.getRepeats().add(repeat);
		
		repeat.setStyle(RepeatStyle.TRIANGLE_OUTLINE);
		
		Lyric lyric = new Lyric(4, 8, "ア");
		doc.getLyrics().add(lyric);
		
		return doc;
	}

	private KicksDocument createMinimalDocument()
	{
		KicksDocument doc = new KicksDocument();
		return doc;
	}
}
