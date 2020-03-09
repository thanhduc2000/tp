package seedu.duke.record;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void getDate() {
        Appointment appointment = new Appointment("22 July 2020", "12.30pm");
        assertEquals("22 July 2020", appointment.getDate());
    }

    @Test
    void getTime() {
        Appointment appointment = new Appointment("22 July 2020", "12:30pm");
        assertEquals("12:30pm", appointment.getTime());
    }

    @Test
    void testToString() {
        Appointment appointment = new Appointment("22 July 2020", "12.30pm");
        assertEquals("{[Date]: 22 July 2020 |[Time]: 12.30pm}", appointment.toString());
    }
}