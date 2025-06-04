function findBlood() {
    const bloodType = document.getElementById('bloodGroupSelect').value;
    const resultDiv = document.getElementById('result');

    // Clear previous result
    resultDiv.textContent = "";

    if (!bloodType) {
        resultDiv.textContent = "Please select a blood group.";
        return;
    }

    fetch(`/api/blood-inventory/${bloodType}`)
        .then(response => {
            if (!response.ok) {
                // If response is not 200, treat as "not found"
                return { quantity: 0 };
            }
            return response.json();
        })
        .then(data => {
            if (data && data.quantity !== undefined) {
                resultDiv.textContent = `Available Quantity for ${bloodType}: ${data.quantity} units`;
            } else {
                resultDiv.textContent = `Available Quantity for ${bloodType}: 0 units`;
            }
        })
        .catch(err => {
            console.error(err);
            resultDiv.textContent = `Available Quantity for ${bloodType}: 0 units`;
        });
}
