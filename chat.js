import * as common from "./common.js"

let socket;
window.onload = start

function appendConvoImg(imgdata)
{
	let img = document.createElement("img");
	img.setAttribute("src", imgdata)
	img.setAttribute("width", 200);
	img.setAttribute("heigth", 200);

	let chat_div = document.getElementById("chat");
	let d = document.createElement("div");
	d.appendChild(img);
	chat_div.appendChild(d);
}
function appendConvo(str)
{
	let chat_div = document.getElementById("chat");
	let d = document.createElement("div");
	let t = document.createTextNode(str);
	d.appendChild(t);
	chat_div.appendChild(d);
}
document.getElementById('uploadForm').addEventListener('submit', function(event) {
	event.preventDefault(); // Prevent the default form submission

	// Get the selected file from the input element
	const fileInput = document.getElementById('fileInput');
	const file = fileInput.files[0];

	if (file) {

		const formData = new FormData();
		formData.append('image', file);
		 
		const reader = new FileReader();

		reader.onload = function(event) {
			const arrayBuffer = event.target.result;

			let url = new URL(window.location.href)
			let u = url.searchParams.get("u")
			if(u == undefined)
				u = "adm"
			//let base64 = arrayBufferToBase64(arrayBuffer);

			// Use the fetch API to send the raw bytes to the server
			fetch(`http://${common.IP}:42069/sendimg?u=${u}`,{
				method: 'POST',
				headers: {
					'Content-Type': 'application/octet-stream',
					'Content-Disposition': `attachment; filename="${file.name}"`
				},
				body: arrayBuffer
			})
		}


		reader.readAsDataURL(file);
	} else {
		console.error('No file selected');
	}
});
function arrayBufferToBase64(buffer) {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
}
function connectToSocket()
{
}
function start()
{
	let socket_img = common.createWSConnection((data)=>{
		appendConvoImg(data.data)
	}, "/img")
	let socket = common.createWSConnection((data)=>{
		appendConvo(data.data)
	})
	// Connection opened


	let chat_sendbutton = document.createElement("button");
	chat_sendbutton.addEventListener("click", (e)=>{
		let chat_tarea        = document.getElementById("chat_tarea");

		let url = new URL(window.location.href)
		let u = url.searchParams.get("u")
		if(u == undefined)
			u = "adm"
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

