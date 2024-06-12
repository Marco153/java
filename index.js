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
	serv.setAttribute("style", "margin-bottom: 10px; margin-right: 5px; background-color: #ee4444; width: auto; border-radius: 5px;")
	doc.appendChild(serv);
	id.id += 1;
}

function textNodeInDiv(txt)
{
	let div = document.createElement("div")
	div.appendChild(document.createTextNode(txt))
	return div
}
async function insertPromoProducts(name, img, price, new_price)
{
	let border_radius = 5;
	let actual_ret   = document.createElement("div");
	actual_ret.setAttribute("class", "promo_product")
	actual_ret.setAttribute("style", `background-color: white; width: 150px; border-top-right-radius: ${border_radius}px; border-radius: ${border_radius}px; display: grid;`);
	let top   = document.createElement("div");
	top.setAttribute("style", `background-color: #772222; color: white; border-top-right-radius: ${border_radius}px; border-top-left-radius: ${border_radius}px; height: 20px; width: 100%;`)

	let price_int = parseInt(price)
	let new_price_int = parseInt(new_price)

	let diff = price_int - new_price_int
	let percent = (diff / price_int) * 100

	let percent_el = document.createTextNode(`${Math.trunc(percent)}% off`)
	top.appendChild(percent_el)
	actual_ret.appendChild(top)


	let ret   = document.createElement("div");
	let inner = document.createElement("div");

	//inner.appendChild(top)
	let img_el = await common.createImgEl(img, 100, 100)

	inner.appendChild(img_el);
	
	let old_price = textNodeInDiv("$"+price)
	old_price.setAttribute("style", "color: #888888; font-size: 12px; text-decoration: line-through;")
	let new_price_dom = textNodeInDiv("$"+new_price)
	new_price_dom.setAttribute("style", "color: #00aa11;")

	inner.appendChild(textNodeInDiv(name))
	inner.appendChild(old_price)
	inner.appendChild(new_price_dom)

	inner.setAttribute("style", "")

	ret.setAttribute("style", "background-color: white; display: flex; justify-content: center; width: 150px; border-radius: 5px; display: grid;");

	ret.appendChild(inner)

	actual_ret.appendChild(ret)
	

	return actual_ret;
}
async function insertPromo(name)
{
	const res = await fetch(`http://${common.IP}:42069/dbpromo?tb=${name}`)
	let json = await res.json()

	if(json.ok == 0)
		return null

	json = json.db

	let dom = document.getElementById("promo")

	for(let i = 0; i < json.length; i++)
	{
		let it = json[i];
		let promo_product = await insertPromoProducts(it.name, it.image, it.price, it.new_price)
		dom.appendChild(promo_product)

	}
	
}

async function start()
{
	await common.insertHeader()
	await common.insertNewButtonInHeader("logar", "/login");

	let maq = await common.createImgEl("maquina.png", 200, 200)
	let page_el = document.getElementById("logo");
	page_el.appendChild(maq)

	serv_desc = document.getElementById("col2");
	servs = ["este e um servico de barra, no qual é feito um corte para dimiluir s tamanho da manga, pé da calça etc.", "Pinça sao apertos de roupas, geralmente uma pequena costura na cintura ou de baixo pra cima", "costuras em geral,  fechaments de buracos, troca de ziper etc."]
	let id = {id: 0};
	insertServ("barra", id);
	insertServ("pinca", id);
	insertServ("costuras", id);
	
	insertPromo("promos")
	
	
}
