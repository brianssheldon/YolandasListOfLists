package org.bubba.yolandaslistoflists.prefs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class PrefsDao
{
	private static final String PREFS_BO = "prefsbo";
	
	public static PrefsBO readFile(Context context)
	{
		PrefsBO bo;
		try
		{
			FileInputStream fis = context.openFileInput(PREFS_BO);
	    	ObjectInputStream in = new ObjectInputStream(fis);
	    	bo = (PrefsBO) in.readObject();
	    	in.close();
	    	fis.close();
	    	System.err.println("read prefs: " + bo.toString());
		}
		catch (Exception e)
		{
			try
			{
				bo = new PrefsBO();

				FileOutputStream fos = context.openFileOutput(PREFS_BO, Context.MODE_PRIVATE);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(bo);
				out.close();
				fos.close();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				bo = new PrefsBO();
			}
		}
		return bo;
	}

	public static void writeFile(PrefsBO bo, Context context)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(PREFS_BO, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(bo);
			out.close();
			fos.close();
	    	System.err.println("write prefs: " + bo.toString());
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
	}
}