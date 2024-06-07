import * as common from "./common.js"
window.onload = start
let cur_serv_selected = 0;
let servs;
let serv_desc = null;
let on_desktop = true;

window.onresize = ()=>
{
	if(window.innerWidth < 600)
	{
		on_desktop = false;
	}
	// desktops
	else
	{
		if(!on_desktop)
		{
			let serv = document.getElementById("services");
			serv_desc.parentNode.removeChild(serv_desc);
			serv.appendChild(serv_desc);
		}
		on_desktop = true;
	}
}

function insertItem(str)
{
	let el = document.createElement("div")
	let text = document.createTextNode(str);
	el.appendChild(text);

	document.getElementById("products").appendChild(el);
}
function insertServ(name, id)
{
	let doc = document.getElementById("col");
	let serv = document.createElement("div");
	let serv_but = document.createElement("div");
	//serv_but.setAttribute("style", "height: 10px")
	serv_but.setAttribute("servid", id.id);
	serv_but.addEventListener("click", (e)=>{

		// mobile
		if(window.innerWidth < 600)
		{
			serv_desc.parentNode.removeChild(serv_desc);
			let id = parseInt(e.target.getAttribute("servid"));
			serv_desc.textContent = servs[id]

			e.target.parentNode.appendChild(serv_desc);

			serv_desc.classList.add("serv_desc")
		}
		// desktops
		else
		{
			let col = document.getElementById("col2");
			let id = parseInt(e.target.getAttribute("servid"));
			serv_desc.classList.remove("serv_desc")

			col.textContent = servs[id]
		}

	})
	let t = document.createTextNode(name);
	serv_but.appendChild(t);
	serv.appendChild(serv_but);
	doc.appendChild(serv);
	id.id += 1;
}
function start()
{
	common.insertHeader()
	common.insertNewButtonInHeader("logar", "/login");
	serv_desc = document.getElementById("col2");
	servs = ["este e um servico de barra", "pinca sao apertos de roupas", "costuras em geral"]
	let id = {id: 0};
	insertServ("barra", id);
	insertServ("pinca", id);
	insertServ("costuras", id);
	
}
