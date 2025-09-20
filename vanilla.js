const formId = "YOUR_SAVED_FORM_ID"; // Get this from your form submission logic
const timetableUrl = `http://localhost:8080/api/v1/timetables/generate/${formId}`;

fetch(timetableUrl)
    .then(response => response.json())
    .then(data => {
        // `data` is the JSON array of TimetableEntry objects
        console.log(data);
        // Now, you can use this data to dynamically build and display the timetable table on your webpage.
    })
    .catch(error => console.error("Error fetching timetable:", error));