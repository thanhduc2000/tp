package seedu.duke.storage;


import seedu.duke.converter.TimeConverter;
import seedu.duke.generator.PatientIdManager;
import seedu.duke.record.Appointment;
import seedu.duke.record.Patient;
import seedu.duke.ui.Ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Storage {

    public static final String PATIENT_ID_SAVE_FILEPATH = "saves/patientId.txt";
    public static final String WHITESPACE = " ";
    private static final String SAVE_DIRECTORY = "saves";
    private static final String APPOINTMENT_LIST_SAVE_FILEPATH = "saves/appointments.txt";
    private static final String PATIENT_LIST_SAVE_FILEPATH = "saves/patients.txt";
    private static final String PIPE_DELIMITER = " | ";
    private static final String LS = System.lineSeparator();
    private static PatientList patientList;
    private static AppointmentList appointmentList;
    private String appointmentListSaveLocation;
    private String patientListSaveLocation;
    private String patientIdSaveLocation;

    /**
     * Constructor for Storage.
     */

    public Storage() {
        this.appointmentListSaveLocation = APPOINTMENT_LIST_SAVE_FILEPATH;
        this.patientListSaveLocation = PATIENT_LIST_SAVE_FILEPATH;
        this.patientIdSaveLocation = PATIENT_ID_SAVE_FILEPATH;
    }

    /**
     * load save file for Appointments list.
     *
     * @return appointmentListToReturn returns the appointment list in the save file
     * @throws FileNotFoundException this exception occurs when a file is not found
     */
    public List<Appointment> loadSavedAppointments() throws FileNotFoundException, ParseException {
        File appointmentSave = new File(this.appointmentListSaveLocation);
        if (!appointmentSave.exists()) {
            File newDirectory = new File(SAVE_DIRECTORY);
            boolean isNewDirectoryCreated = newDirectory.mkdir();
            if (isNewDirectoryCreated || newDirectory.exists()) {
                File newFile = new File(APPOINTMENT_LIST_SAVE_FILEPATH);
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("here 1");
                    Ui.showFailedToCreateFile();
                }
            } else {
                System.out.println("here 2");
                Ui.showFailedToCreateDirectory();
            }
            throw new FileNotFoundException();
        }
        List<Appointment> appointmentListToReturn = new ArrayList<>();
        Scanner s = new Scanner(appointmentSave);
        while (s.hasNext()) {
            //process each line, construct new Appointment object
            String appointmentString = s.nextLine();
            String[] patientFields = appointmentString.split(" \\| ", 3);
            //assert patientFields.length == 2 : "not enough fields in this line:" + appointmentString;
            for (String field : patientFields) {
                if (field.trim().isEmpty()) {
                    field = null;
                }
            }

            String convertedDate = TimeConverter.convertDate(patientFields[0]);
            String convertedTime = TimeConverter.convertTime(patientFields[1]);

            Appointment newAppointmentToLoad =
                    new Appointment(convertedDate, convertedTime, Integer.parseInt(patientFields[2]));
            appointmentListToReturn.add(newAppointmentToLoad);
        }

        return appointmentListToReturn;
    }

    /**
     * load save file for Patients list.
     *
     * @return patientListToReturn the patient list for the save file.
     * @throws FileNotFoundException this exception occurs if a file is not found.
     */
    public List<Patient> loadSavedPatients() throws FileNotFoundException {
        File patientSave = new File(this.patientListSaveLocation);
        if (!patientSave.exists()) {
            File newDirectory = new File(SAVE_DIRECTORY);
            boolean isNewDirectoryCreated = newDirectory.mkdir();
            if (isNewDirectoryCreated || newDirectory.exists()) {
                File newFile = new File(PATIENT_LIST_SAVE_FILEPATH);
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("here 3");
                    Ui.showFailedToCreateFile();
                }

            } else {
                System.out.println("here 4");
                Ui.showFailedToCreateDirectory();
            }
            throw new FileNotFoundException();

        }
        List<Patient> patientListToReturn = new ArrayList<>();
        Scanner s = new Scanner(patientSave);
        while (s.hasNext()) {
            //process each line, construct new Appointment object
            String patientString = s.nextLine();
            int delimiterCount = 0;
            for (int i = 0; i < patientString.length(); i++) {
                if (Character.toString(patientString.charAt(i)).equals("|")) {
                    delimiterCount++;
                }
            }
            //assert delimiterCount == 3 : "not enough fields in this line:";
            String[] patientFields = patientString.split(" \\| ", 5);
            for (String field : patientFields) {
                if (field.trim().isEmpty()) {
                    field = null;
                }
            }
            if (patientFields[1].isEmpty()) {
                patientFields[1] = "-1";
            }
            Patient newPatientToLoad =
                    new Patient(patientFields[0], Integer.parseInt(patientFields[1]), patientFields[2],
                            patientFields[3], Integer.parseInt(patientFields[4]));
            patientListToReturn.add(newPatientToLoad);

        }

        loadPatientIdState();

        return patientListToReturn;
    }

    /**
     * Load the state of the patient id(s) into our patient id management system.
     *
     * @throws FileNotFoundException if there is an error locating the file to save to.
     */
    private void loadPatientIdState() throws FileNotFoundException {

        /* Preparing the file reader */
        File patientIdSave = new File(this.patientIdSaveLocation);
        if (!patientIdSave.exists()) {
            File newDirectory = new File(SAVE_DIRECTORY);
            boolean isNewDirectoryCreated = newDirectory.mkdir();
            if (isNewDirectoryCreated || newDirectory.exists()) {
                File newFile = new File(PATIENT_ID_SAVE_FILEPATH);
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    Ui.showFailedToCreateFile();
                }

            } else {
                System.out.println("here 5");
                Ui.showFailedToCreateDirectory();
            }
            throw new FileNotFoundException();

        }

        /* Actual file reading */
        Scanner s = new Scanner(patientIdSave);

        /* There is information saved about the patientIds */
        if (s.hasNext()) {
            PatientIdManager.setNextTopNewNumber(Integer.parseInt(s.nextLine()));
        }

        /* There are reusable patient id*/
        if (s.hasNext()) {
            String patientIdString = s.nextLine();
            String[] patientIdStringArray = patientIdString.split(WHITESPACE);

            for (String number : patientIdStringArray) {
                PatientIdManager.addBackPatientId(Integer.parseInt(number));
            }
        }
    }

    /**
     * This method saves the patient's appointment details from the AppointmentList into the local save file.
     *
     * @throws IOException this exception occurs if the patient's appointment details are unable to be written
     *                     to the local save file.
     */
    public void saveAppointmentsList() throws IOException, ParseException {
        FileWriter fwAppointmentSave;
        try {
            fwAppointmentSave = new FileWriter(this.appointmentListSaveLocation);
        } catch (IOException e) {
            throw new IOException();
        }
        String newAppointmentString = null;

        for (int i = 0; i < AppointmentList.getTotalAppointments(); i++) {
            Appointment newAppointmentData = AppointmentList.getAppointmentRecord(i);
            newAppointmentString =
                    newAppointmentData.getDate() + PIPE_DELIMITER + newAppointmentData.getTime() + PIPE_DELIMITER
                            + newAppointmentData.getPatientId() + LS;
            fwAppointmentSave.write(newAppointmentString);

        }

        fwAppointmentSave.close();
    }

    /**
     * This method saves the patient list into the local save file.
     *
     * @throws IOException this exception occurs if the patient data was unable to be written to the local save file.
     */
    public void savePatientList() throws IOException {


        FileWriter fwPatientSave;
        try {
            fwPatientSave = new FileWriter(this.patientListSaveLocation);
        } catch (IOException e) {
            throw new IOException();
        }
        String newPatientString = null;

        for (int i = 0; i < PatientList.getTotalPatients(); i++) {
            Patient newPatientData = PatientList.getPatientRecord(i);
            newPatientString = newPatientData.getName() + PIPE_DELIMITER + newPatientData.getAge() + PIPE_DELIMITER
                    + newPatientData.getAddress() + PIPE_DELIMITER + newPatientData.getContactNumber() + PIPE_DELIMITER
                    + newPatientData.getPatientID() + LS;
            fwPatientSave.write(newPatientString);

        }

        savePatientIdState();
        fwPatientSave.close();
    }

    /**
     * Save the patient id management system state.
     *
     * @throws IOException if the file cannot be written for some reason
     */
    private void savePatientIdState() throws IOException {

        /* Preparing file writer*/
        FileWriter fwPatientIdSave = null;
        try {
            fwPatientIdSave = new FileWriter(this.patientIdSaveLocation);
        } catch (IOException e) {
            throw new IOException();
        }

        int tempNextTopNumber = PatientIdManager.getNextTopNewNumber();
        Queue<Integer> tempQueue = PatientIdManager.getNextNumberQueueThing();

        /* Saving of actual patient id management system state data */
        fwPatientIdSave.write(tempNextTopNumber + LS);
        if (!tempQueue.isEmpty()) {
            for (Integer number : tempQueue) {
                fwPatientIdSave.write(number + WHITESPACE);
            }
        }
        fwPatientIdSave.close();


    }

}
