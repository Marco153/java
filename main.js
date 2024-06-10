import * as common from "./common.js"

let db_doms = [];
window.onload = start
let IP = `http://${common.IP}:42069`
let last_id;
let cur_img_edit_tarea = null;

let main_db_dom;
let promos_db_dom;

async function renderDb(db_orig, dom_idx)
{
	let db = db_orig.db;
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
		//columns.forEach((value) =>{
		for(const value of columns)
		{
			let td = document.createElement("td");
			let tarea = "";
			if(value == "id")
				tarea = document.createTextNode(db[i][value]);
			else if(value == "image")
			{
				tarea = await common.createImgEl(db[i][value], 100, 100)

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
		}

		let remove = document.createElement("td");
		let remove_button = document.createElement("button");
		remove_button.textContent = "remove";
		remove_button.setAttribute("dbid", db[i].id);
		remove_button.setAttribute("class", "rm_but")
		remove_button.addEventListener("click", async (e)=>{
			let id = parseInt(e.target.getAttribute("dbid"));
			let d = db_orig;

			for(let i = 0; i < db.length; i++)
			{
				if(db[i].id == id)
					db.splice(i, 1);
			}

			const response = await fetch(`${IP}/dbrm?id=${id}&tb=${d.name}`)
			let val = await getDataBase(d.name)
			let which_db_id = parseInt(e.target.getAttribute("which_db_id"));
			renderDb(val, which_db_id);

		});
		remove_button.setAttribute("which_db_id", dom_idx);


		remove.appendChild(remove_button);
		//thead.setAttribute("class", "table_head");
		thead.appendChild(remove);

		

		table.appendChild(thead);
	}
	let add_button = document.createElement("button");
	add_button.setAttribute("class", "add_but");
	add_button.addEventListener("click", async (e) =>{
		let d = db_orig;
		fetch(`${IP}/dbadd?id=${last_id + 1}&tb=${d.name}`)

		let val = await getDataBase(d.name)
		let which_db_id = parseInt(e.target.getAttribute("which_db_id"));
		renderDb(val, which_db_id);
	})
	add_button.textContent = "add new row";
	add_button.setAttribute("which_db_id", dom_idx);

	let up_button = document.createElement("button");
	up_button.setAttribute("class", "up_but");
	up_button.addEventListener("click", () =>{
		let d = db_orig;
		fetch(`${IP}/dbup?tb=${d.name}`, 
			{method: 'POST',
			body: JSON.stringify(db)}
	)})
	up_button.textContent = "update";

	let all = document.createElement("div");

	//document.body.textContent = "";
	all.appendChild(add_button);
	all.appendChild(up_button);
	all.appendChild(table);
	all.setAttribute("class", "all");

	
	let dom = db_doms[dom_idx]
	dom.textContent = ""
	dom.appendChild(all);
}
async function start()
{
	db_doms.push(document.getElementById("main"))
	db_doms.push(document.getElementById("promos"))

	let val = await getDataBase("product");
	renderDb(val, 0);

	val = await getDataBase("promos");
	renderDb(val, 1);


	//document.getElementById("main").innerHTML += "added by js"
	//db = val.db;

	
}
async function getDataBase(table_name)
{
	let ret = "";
	try
	{
		let response = await fetch(`${IP}/dball?tb=${table_name}`)
		ret = await response.json();
		ret = {name: table_name, db: ret.db};
	}
	catch(error)
	{
		console.log(error);
	}

	return ret;
}

