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
function start()
{
	socket = new WebSocket(`ws://${common.IP}:42070`);
	// Connection opened
	socket.addEventListener("open", (event) => {
		console.log("is open")
		socket.send("Hello Server!");
	});

	// Listen for messages
	socket.onmessage = (event) => {
		console.log("Message from server ", event.data);
		appendConvo(event.data);
	};

	let chat_sendbutton = document.createElement("button");
	chat_sendbutton.addEventListener("click", (e)=>{
		let chat_tarea        = document.getElementById("chat_tarea");

		fetch(`http://${common.IP}:42069/send?m=${chat_tarea.value}`)
		chat_tarea.value = "";
	})
	chat_sendbutton.appendChild(document.createTextNode("Send"));

	document.body.appendChild(chat_sendbutton);
	//socket.send("whats up player?");
}

