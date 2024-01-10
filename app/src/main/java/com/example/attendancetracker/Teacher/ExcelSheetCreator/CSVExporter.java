package com.example.attendancetracker.Teacher.ExcelSheetCreator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public static void exportToCSV(Context context, List<StudentModel> studentDataList) {
        try {
            File file = new File(context.getExternalFilesDir(null), "AttendanceSheet.csv");
            FileWriter fw = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(fw);

            // Add header to CSV
            csvWriter.writeNext(new String[]{"Name", "Roll Number", "Year", "Department"});

            // Add data to CSV
            for (int i = 0; i < studentDataList.size(); i++) {
                StudentModel studentData = studentDataList.get(i);

                csvWriter.writeNext(new String[]{studentData.getName(), studentData.getRollnumber(), studentData.getWhichYear(), "IT"});
            }

            csvWriter.close();

            shareFile(context, file);

            Toast.makeText(context, "CSV file created and saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating CSV file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareFile(Context context, File file) {
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use FileProvider for scoped storage on Android 10+
                uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            } else {
                // Use Uri.fromFile for non-scoped storage
                uri = Uri.fromFile(file);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("text/csv");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//            if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
//            } else {
//                Toast.makeText(context, "No app available for sharing", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error sharing file", Toast.LENGTH_SHORT).show();
        }
    }


}

