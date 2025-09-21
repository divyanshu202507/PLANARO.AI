document.getElementById('downloadSource').addEventListener('click', function(event) {
  event.preventDefault();

  // Get the entire HTML source code as a string
  // You can customize this to get any source you want to offer for download
  const sourceCode = document.documentElement.outerHTML;

  // Create a blob with the source code text
  const blob = new Blob([sourceCode], {type: 'text/html'});

  // Create a temporary anchor to trigger download
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'index.html';  // Filename for the downloaded file
  document.body.appendChild(a);
  a.click();

  // Cleanup
  document.body.removeChild(a);
  URL.revokeObjectURL(a.href);
});
