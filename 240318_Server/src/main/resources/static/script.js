const produktForm = document.querySelector('form');
const produktInput = document.getElementById('produkt-input');
const produktListUL = document.getElementById('produkt-list');


let allProdukte = getProdukte();
updateProduktList();

produktForm.addEventListener('submit', function (e){
    e.preventDefault();
    addProdukt();
})

function createProdukt(id, isChecked, anzahl, produkt) {
    return {
        id: id,
        isChecked: isChecked,
        anzahl: anzahl,
        produkt: produkt,
        getId: function() {
            return this.id;
        },
        getStatus: function() {
            return this.isChecked;
        },
        getAnzahl: function() {
            return this.anzahl;
        },
        getProdukt: function() {
            return this.produkt;
        },
        setId: function(id) {
            this.id = id;
        },
        setStatus: function(isChecked) {
            this.isChecked = isChecked;
        },
        setAnzahl: function(anzahl) {
            this.anzahl = anzahl;
        },
        setProdukt: function(produkt) {
            this.produkt = produkt;
        }
    };
}

function addProdukt(){
    const produktText = produktInput.value.trim();

    //nur wenn der Benutzer etwas eingegeben hat
    if(produktText.length > 0){
        const produktObject = {
            text: produktText,
            completed: false
        }
        allProdukte.push(produktObject);
        updateProduktList();
        saveProdukte();
        produktInput.value = "";
    }
}

function updateProduktList(){
    produktListUL.innerHTML = "";
    allProdukte.forEach((produkt, produktIndex)=>{
        produktItem = createProduktItem(produkt, produktIndex);
        produktListUL.append(produktItem);
    })
}

function createProduktItem(produkt, produktIndex){
    const produktId = "produkt-"+produktIndex;
    const produktLI = document.createElement("li");
    const produktText = produkt.text;

    //className auf "produkt" damit die CSS-Styles wirken
    produktLI.className = "produkt";
    produktLI.innerHTML = ` 
                <input type="checkbox" id="${produktId}">
                <label class="custom-checkbox" for="${produktId}">
                    <svg fill="transparent" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
                        <path d="M382-240 154-468l57-57 171 171 367-367 57 57-424 424Z"/>
                    </svg>
                </label>
                <label for="${produktId}" class="produkt-text">
                    ${produktText}
                </label>
                <button class="delete-button">
                    <svg fill="var(--secondary-color)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24">
                        <path d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z"/>
                    </svg>
                </button>
    `
    const deleteButton = produktLI.querySelector(".delete-button");
    deleteButton.addEventListener("click", ()=>{
        deleteProduktItem(produktIndex);
    })
    const checkbox = produktLI.querySelector("input");
    checkbox.addEventListener("change", ()=>{
        allProdukte[produktIndex].completed = checkbox.checked;
        saveProdukte();
    })
    checkbox.checked = produkt.completed;

    return produktLI;
}

function deleteProduktItem(produktIndex){
    allProdukte = allProdukte.filter((_, i)=> i !== produktIndex);
    saveProdukte();
    updateProduktList();
}

function saveProdukte(){
    const produkteJson = JSON.stringify(allProdukte);
    localStorage.setItem("produkte", produkteJson)
}

function getProdukte(){
    /*const produkte = localStorage.getItem("produkte") || "[]";
    return JSON.parse(produkte);*/

    fetch('http://localhost:8080/api/produkte')
        .then(response => response.json())
        .then(data => {
            console.log('Alle Produkte:', data);
            return JSON.parse(data);
        })
        .catch(error => console.error('Fehler beim Abrufen der Produkte:', error));
}
