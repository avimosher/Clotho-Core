
package uk.ac.sanger.artemis.io;

import java.lang.IndexOutOfBoundsException;
import java.lang.String;

import org.clothocore.api.data.Part;
import uk.ac.sanger.artemis.util.ReadOnlyException;

public class ClothoSequence implements Sequence
{
    private Part part;

    private int aCount;
    private int cCount;
    private int gCount;
    private int tCount;

    private boolean sequenceIsStale = true;

    public ClothoSequence(final Part part)
    {
	this.part = part;
    }
  
    public char charAt(int i)
    {
      return ' ';
    }
   
    public int length()
    {
        return part.getSeq().seqLength();
    }

    public int getACount()
    {
	if (sequenceIsStale)
	    countSymbols();

	return aCount;
    }

    public int getCCount()
    {
	if (sequenceIsStale)
	    countSymbols();

	return cCount;
    }

    public int getGCount()
    {
	if (sequenceIsStale)
	    countSymbols();

	return gCount;
    }

    public int getTCount()
    {
	if (sequenceIsStale)
	    countSymbols();

	return tCount;
    }

    public int getOtherCount()
    {
	if (sequenceIsStale)
	    countSymbols();

	return (length() - aCount - cCount - gCount -tCount);
    }

    public String getSubSequence(int index1, int index2)
    {
	String subSeq = "";

	try
	{
	    subSeq = part.getSeq().getSeq().toLowerCase().substring(index1-1, (index2-index1)+1);
	}
	catch (IndexOutOfBoundsException ioe)
	{
	    System.err.println("An error occurred while extracting subsequence "
			       + ioe.getMessage());
	    ioe.printStackTrace();
	}

	return subSeq;
    }

  public char[] getCharSubSequence (int start, int end)
  {
    return getSubSequence(start,end).toCharArray();
  }


  public void setFromChar(final char[] seqString)
        throws ReadOnlyException
  {
    //Edit ed = new Edit(1, length (), DNATools.createDNA(new String(seqString)));
    /*try {
      symbols.edit(ed);
    } catch (ChangeVetoException e) {
      throw new ReadOnlyException ("cannot set sequence - readonly");
    } catch (IllegalAlphabetException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }*/
  }

  public void clear()
  {
  }
  
    private void countSymbols()
    {
      final String seq = part.getSeq().getSeq().toLowerCase();
      
      int a = 0;
      int c = 0;
      int g = 0;
      int t = 0;

      for (int i = 0 ; i < seq.length () ; ++i) {
        char token = seq.charAt(i);

	    switch (token)
	    {
		case 'a':
		    a++;
		    break;

		case 'c':
		    c++;
		    break;

		case 'g':
		    g++;
		    break;

		case 't':
		    t++;
		    break;

		default:
		    break;
	    }
	}

	aCount = a;
	cCount = c;
	gCount = g;
	tCount = t;

        sequenceIsStale = false;
    }
}
