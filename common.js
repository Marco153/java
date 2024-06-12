export const IP = "192.168.1.2"
export const fetch_IP = `http://${IP}:42069`


export async function createWSConnection(on_message_func, endpoint)
{
	let endp_str = ""
	if(endpoint != undefined)
		endp_str = endpoint

	let socket = new WebSocket(`ws://${IP}:42070${endp_str}`);
	if(socket == null)
	{
		//sessionStorage.setItem("socket", {val: socket})
	}
	socket.addEventListener("open", (event) => {
		console.log("is open")
		socket.send("Hello Server!");
	});
	// Listen for messages
	socket.onmessage = on_message_func
}
export async function headerBut(name, link, icon_link)
{
	let ret = document.createElement("div");
	ret.setAttribute("class", "header_class")
	let t = document.createTextNode(name);
	ret.addEventListener("click", ()=>{
		window.location.href = link;
	})
	let icon = await createImgEl(icon_link, 20, 20);
	icon.setAttribute("style", "border-radius: 20px; position: relative; top: 5px;")
	ret.appendChild(icon)
	ret.appendChild(t);



	ret.setAttribute("class", "header_but")
	return ret;
}

export async function insertNewButtonInHeader(name, link, icon_link)
{
	let h = document.getElementById("header_id");
	let el = await headerBut(name, link);
	h.appendChild(el)
}
export async function createImgEl(link, w, h)
{
	//let div = document.createElement("div")

	let base64img = ""
	if(link != undefined && link != null && link != "null")
	{
		try
		{
			base64img = await fetch(`${fetch_IP}/${link}`)
			base64img = await base64img.text();
		}
		catch(err)
		{
			base64img = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fralfvanveen.com%2Fen%2Fglossary%2Fplaceholder%2F&psig=AOvVaw0eRNhf6pkf9Vwv9MTWIuNp&ust=1718242138349000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCJjxq_T01IYDFQAAAAAdAAAAABAE"
		}
	}
	else
	{
		base64img = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fralfvanveen.com%2Fen%2Fglossary%2Fplaceholder%2F&psig=AOvVaw0eRNhf6pkf9Vwv9MTWIuNp&ust=1718242138349000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCJjxq_T01IYDFQAAAAAdAAAAABAE"
	}

	let img = document.createElement("img");
	img.setAttribute("src", base64img)
	img.setAttribute("width", w);
	img.setAttribute("heigth", h);

	//div.appendChild(img)

	return img
}
export async function insertHeader()
{
	let h = document.createElement("div")

	h.setAttribute("id", "header_id")

	h.appendChild(await headerBut("inicio", "/", "home_icon.png"))

	document.body.appendChild(h);
}
