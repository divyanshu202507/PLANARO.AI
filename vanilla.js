
document.getElementById('timetableForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevents the default form submission behavior

    const formId = document.getElementById('formId').value;
    const timetableDisplay = document.getElementById('timetableDisplay');

    // Clear any previous timetable data
    timetableDisplay.innerHTML = '<p>Loading timetable...</p>';

    // Make an API call to your Spring Boot backend
    fetch(`http://localhost:8080/api/v1/timetables/generate/${formId}`)
        .then(response => {
            if (!response.ok) {
                // If the response is not successful, throw an error
                throw new Error('Timetable form not found or an error occurred.');
            }
            return response.json(); // Parse the JSON response
        })
        .then(data => {
            // Check if data is an array and not empty
            if (Array.isArray(data) && data.length > 0) {
                renderTimetable(data); // Call a function to render the data
            } else {
                timetableDisplay.innerHTML = '<p>No timetable entries found.</p>';
            }
        })
        .catch(error => {
            // Handle any errors that occurred during the fetch
            console.error('Error fetching timetable:', error);
            timetableDisplay.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
        });
});

function renderTimetable(timetableEntries) {
    const timetableDisplay = document.getElementById('timetableDisplay');
    timetableDisplay.innerHTML = ''; // Clear the "Loading" message

    // Create a table element
    const table = document.createElement('table');
    table.classList.add('timetable-table');

    // Create table header
    const tableHeader = `
        <thead>
            <tr>
                <th>Day</th>
                <th>Subject</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Room</th>
            </tr>
        </thead>
    `;
    table.innerHTML = tableHeader;

    // Create table body
    const tableBody = document.createElement('tbody');
    timetableEntries.forEach(entry => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${entry.day}</td>
            <td>${entry.subject}</td>
            <td>${entry.startTime}</td>
            <td>${entry.endTime}</td>
            <td>${entry.room}</td>
        `;
        tableBody.appendChild(row);
    });
    table.appendChild(tableBody);

    // Append the table to the display container
    timetableDisplay.appendChild(table);
}