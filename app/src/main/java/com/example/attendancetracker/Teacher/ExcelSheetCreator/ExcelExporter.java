//package com.example.attendancetracker.Teacher.ExcelSheetCreator;
//
//import android.content.Context;
//import android.os.Environment;
//import android.widget.Toast;
//
//import com.example.attendancetracker.Student.StudentModel.StudentModel;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//public class ExcelExporter {
//
//    public static void exportToExcel(Context context, List<StudentModel> studentDataList) {
//        // Create a new workbook
//        Workbook workbook = new XSSFWorkbook();
//
//        // Create a sheet in the workbook
//        Sheet sheet = workbook.createSheet("AttendanceSheet");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("Name");
//        headerRow.createCell(1).setCellValue("Roll Number");
////        headerRow.createCell(2).setCellValue("Date of Attendance");
//        headerRow.createCell(2).setCellValue("Department");
//
//        // Fill data rows
//        for (int i = 0; i < studentDataList.size(); i++) {
//            StudentModel studentData = studentDataList.get(i);
//            Row dataRow = sheet.createRow(i + 1);
//            dataRow.createCell(0).setCellValue(studentData.getName());
//            dataRow.createCell(1).setCellValue(studentData.getRollnumber());
////            dataRow.createCell(2).setCellValue(studentData.getDateOfAttendance());
//            dataRow.createCell(2).setCellValue(studentData.getDepartment());
//        }
//
//        // Save the workbook to a file
//        File file = saveWorkbook(workbook, "AttendanceSheet.xlsx");
//
//        // Notify the user that the file has been saved
//        if (file != null) {
//            Toast.makeText(context, "Excel file saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(context, "Error saving Excel file", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private static File saveWorkbook(Workbook workbook, String fileName) {
//        File file = null;
//        try {
//            // Get the directory for the app's private files
//            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AttendanceApp");
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Create the file
//            file = new File(directory, fileName);
//            file.createNewFile();
//
//            // Write the workbook content to the file
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            workbook.write(fileOutputStream);
//            fileOutputStream.close();
//
//            // Close the workbook
//            workbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return file;
//    }
//}
