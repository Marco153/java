export const IP = "localhost"
export const fetch_IP = `http://${IP}:42069`

function headerBut(name, link)
{
	let ret = document.createElement("div");
	ret.setAttribute("class", "header_class")
	let t = document.createTextNode(name);
	ret.addEventListener("click", ()=>{
		window.location.href = link;
	})
	ret.appendChild(t);

	ret.setAttribute("class", "header_but")
	return ret;
}

export function insertHeader()
{
	let h = document.createElement("div")

	h.setAttribute("class", "header_class")

	h.appendChild(headerBut("inicio", "/"));
	h.appendChild(headerBut("logar", "/login"));

	document.body.appendChild(h);
}
