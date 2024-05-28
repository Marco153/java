window.onload = renderDb
let IP = "http://localhost:42069"
let db;
let last_id;

async function renderDb()
{
	let val = await getDataBase();


	db = val.db;
	last_id = -1;

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
		let id = parseInt(db[i].id);
		if(id > last_id)
			last_id = id;

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
				tarea.setAttribute("dbid", db[i].id);
				tarea.setAttribute("dbcol", value);
				tarea.addEventListener("input", (e)=>{
					let id = parseInt(e.target.getAttribute("dbid"));
					let col = e.target.getAttribute("dbcol");

					db[i][col] = e.target.value;
				});
			}

			td.appendChild(tarea);
			thead.appendChild(td);
		})
		let remove = document.createElement("td");
		let remove_button = document.createElement("button");
		remove_button.textContent = "x";
		remove_button.setAttribute("dbid", db[i].id);
		remove_button.addEventListener("click", async (e)=>{
			let id = parseInt(e.target.getAttribute("dbid"));

			for(let i = 0; i < db.length; i++)
			{
				if(db[i].id == id)
					db.splice(i, 1);
			}

			const response = await fetch(`${IP}/dbrm?id=${id}`)
			renderDb();

		});

		remove.appendChild(remove_button);
		thead.appendChild(remove);

		

		table.appendChild(thead);
	}
	let add_button = document.createElement("button");
	add_button.addEventListener("click", () =>{
		fetch(`${IP}/dbadd?id=${last_id + 1}`)
		renderDb();
	})
	add_button.textContent = "add new row";

	let up_button = document.createElement("button");
	up_button.addEventListener("click", () =>{
		fetch(`${IP}/dbup`, 
			{method: 'POST',
			body: JSON.stringify(db)}
	)})
	up_button.textContent = "update";

	document.body.textContent = "";
	document.body.appendChild(table);
	document.body.appendChild(add_button);
	document.body.appendChild(up_button);
}
async function start()
{
	let val = await getDataBase();


	document.getElementById("main").innerHTML += "added by js"
	db = val.db;

	renderDb();
	
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

