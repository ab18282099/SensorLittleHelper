package com.example.user.soil_supervise_kotlin.OtherClass

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class DataWriter
{
    companion object
    {
        fun WriteData(context: Context, fileName: String, strWrite: String?)
        {
            try
            {
                val mSDFile: File
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED))
                {
                    Toast.makeText(context, "Can't find SD-Card", Toast.LENGTH_SHORT).show()
                    return
                }
                else
                {
                    mSDFile = Environment.getExternalStorageDirectory()
                }

                if (mSDFile != null)
                {
                    val mFile = File(mSDFile.parent + "/" + mSDFile.name + "/DATA")

                    if (!mFile.exists())
                    {
                        mFile.mkdirs()
                    }
                    else
                    {
                        val mFileWriter = FileWriter(mSDFile.parent + "/" + mSDFile.name + "/DATA/" + fileName + ".txt")
                        val bufferWriter = BufferedWriter(mFileWriter)

                        bufferWriter.write(strWrite)
//                        bufferWriter.newLine()

                        bufferWriter.close()
                        mFileWriter.close()
                    }
                }
            }
            catch (e: Exception)
            {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}