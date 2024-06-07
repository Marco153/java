export const IP = "localhost"
export const fetch_IP = `http://${IP}:42069`

export function headerBut(name, link)
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

export function insertNewButtonInHeader(name, link)
{
	let h = document.getElementById("header_id");
	h.appendChild(headerBut(name, link))
}
export function insertHeader()
{
	let h = document.createElement("div")

	h.setAttribute("id", "header_id")

	h.appendChild(headerBut("inicio", "/"));

	document.body.appendChild(h);
}
