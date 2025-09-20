fetch("http://localhost:8080/api/timetable/ROLL123")
  .then(res => res.json())
  .then(data => {
    console.log(data);
    // Render timetable in a table format
  });