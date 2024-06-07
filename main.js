import * as common from "./common.js"

window.onload = renderDb
let IP = `http://${common.IP}:42069`
let db;
let last_id;
let cur_img_edit_tarea = null;

async function renderDb()
{
	let val = await getDataBase();


	db = val.db;
	last_id = -1;

	let columns = Object.getOwnPropertyNames(db[0]);

	let table = document.createElement("table")

	let thead = document.createElement("tr");
	thead.setAttribute("class", "table_head");
	table.appendChild(thead);

	columns.forEach((value) =>{
		
		let td = document.createElement("td");
		let text = document.createTextNode(value);
		td.appendChild(text);
		thead.appendChild(td);
	})

	let td = document.createElement("td");
	let text = document.createTextNode("actions");
	td.appendChild(text);
	thead.appendChild(td);
	
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
			else if(value == "image")
			{
				tarea = document.createElement("img");
				tarea.setAttribute("src", db[i][value]);
				tarea.setAttribute("width", "100");
				tarea.setAttribute("heigth", "100");

				tarea.setAttribute("dbid", db[i].id);
				tarea.setAttribute("dbcol", value);

				tarea.addEventListener("click", (e) =>{
					if(cur_img_edit_tarea)
						cur_img_edit_tarea.remove();
					let id = parseInt(e.target.getAttribute("dbid"));
					let col = e.target.getAttribute("dbcol");

					let edit_area = document.createElement("textarea");
					edit_area.setAttribute("dbid", db[i].id);
					edit_area.setAttribute("dbcol", value);
					edit_area.addEventListener("input", (e)=>{
						let id = parseInt(e.target.getAttribute("dbid"));
						let col = e.target.getAttribute("dbcol");

						db[i][col] = e.target.value;
					});
					edit_area.value = db[i][col];
					e.target.parentNode.appendChild(edit_area);
					cur_img_edit_tarea = edit_area;
				})
			}

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
		remove_button.textContent = "remove";
		remove_button.setAttribute("dbid", db[i].id);
		remove_button.setAttribute("class", "rm_but")
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
		//thead.setAttribute("class", "table_head");
		thead.appendChild(remove);

		

		table.appendChild(thead);
	}
	let add_button = document.createElement("button");
	add_button.setAttribute("class", "add_but");
	add_button.addEventListener("click", () =>{
		fetch(`${IP}/dbadd?id=${last_id + 1}`)
		renderDb();
	})
	add_button.textContent = "add new row";

	let up_button = document.createElement("button");
	up_button.setAttribute("class", "up_but");
	up_button.addEventListener("click", () =>{
		fetch(`${IP}/dbup`, 
			{method: 'POST',
			body: JSON.stringify(db)}
	)})
	up_button.textContent = "update";

	let all = document.createElement("div");

	document.body.textContent = "";
	all.appendChild(add_button);
	all.appendChild(up_button);
	all.appendChild(table);
	all.setAttribute("id", "all");

	document.body.appendChild(all);

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

