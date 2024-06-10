import * as common from "./common.js"

let socket;
window.onload = start

function appendConvo(str)
{
	let chat_div = document.getElementById("chat");
	let d = document.createElement("div");
	let t = document.createTextNode(str);
	d.appendChild(t);
	chat_div.appendChild(d);
}
function connectToSocket()
{
}
function start()
{
	let socket = common.createWSConnection((data)=>{
		appendConvo(data.data)
	})
	// Connection opened


	let chat_sendbutton = document.createElement("button");
	chat_sendbutton.addEventListener("click", (e)=>{
		let chat_tarea        = document.getElementById("chat_tarea");

		let url = new URL(window.location.href)
		let u = url.searchParams.get("u")
		fetch(`http://${common.IP}:42069/send?to=${u}`,
		{
				method: "POST",
				body: chat_tarea.value
		})
		chat_tarea.value = "";
	})
	chat_sendbutton.appendChild(document.createTextNode("Send"));

	document.body.appendChild(chat_sendbutton);
	//socket.send("whats up player?");
}

