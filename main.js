window.onload = start
let IP = "http://localhost:42069"

async function start()
{
	let val = await getDataBase();


	document.getElementById("main").innerHTML += "added by js"
	let db = val.db;

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
	/*
	let trow = document.createElement("tr");
	let td = document.createElement("td");
	let text = document.createElement("input");
	text.setAttribute("type", "text");

	let td2 = document.createElement("td");
	let text2 = document.createTextNode("test2");


	td.appendChild(text);
	td2.appendChild(text2);

	trow.appendChild(td);
	trow.appendChild(td2);
	table.appendChild(trow);
	*/

	
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
				tarea.setAttribute("dbid", i);
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
		table.appendChild(thead);
	}
	let up_button = document.createElement("button");
	up_button.addEventListener("click", () =>{
		fetch(`${IP}/dbup`, 
			{method: 'POST',
			body: JSON.stringify(db)}
	)})
	up_button.textContent = "update";

	document.body.appendChild(table);
	document.body.appendChild(up_button);
	
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
