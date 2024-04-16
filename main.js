window.onload = start
let IP = "http://localhost:42069"
let areas = []
let db = "";

function pushUpdates()
{
	areas.forEach((value) =>{
		let id = value.getAttribute("id");
		let column = value.getAttribute("column");

		for(let i = 0; i < db.length; i++)
		{
			if(db[i].id == id)
			{
				db[i][column] = value.value
				break;
			}
		}
	})
	fetch(`${IP}/dbupdate`, {
		method: "POST",
		body: JSON.stringify(db),
	})
}

async function start()
{
	let val = await getDataBase();


	document.getElementById("main").innerHTML += "added by js"

	db = val.db;

	let columns = Object.getOwnPropertyNames(db[0]);

	let table = document.createElement("table")

	let thead = document.createElement("tr");
	table.appendChild(thead);

	columns.forEach((value) =>{
		
		let td = document.createElement("td");
		let text = document.createTextNode(value);
		td.appendChild(text);
		thead.appendChild(td);
	})
	
	for(let i = 0; i < db.length; i++)
	{
		let thead = document.createElement("tr");
		columns.forEach((value) =>{
			let td = document.createElement("td");
			let tarea = "";
			if(value == "id")
				tarea = document.createTextNode(db[i][value]);
			else
			{
				tarea = document.createElement("textarea");
				tarea.value = db[i][value];
				
				tarea.setAttribute("column", value)
				tarea.setAttribute("id", db[i].id)

				areas.push(tarea)
			}

			td.appendChild(tarea);
			thead.appendChild(td);
		})
		table.appendChild(thead);
	}

	document.body.appendChild(table);

	let update_button = document.createElement("button")
	update_button.addEventListener("click", ()=>{pushUpdates()})
	update_button.textContent = "update"
	document.body.appendChild(update_button);

	
}
async function getDataBase()
{
	let ret = "";
	try
	{
		const response = await fetch(`${IP}/dball`)
		ret = await response.json();
	}
	catch(error)
	{
		console.log(error);
	}

	return ret;
}
