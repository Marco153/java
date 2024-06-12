import * as common from "./common.js"

let socket;
window.onload = start


function appendConvoImg(img_id)
{
	fetch(`http://${common.IP}:42069/getwaitingimg?id=${img_id}`)
	.then((imgdata)=>{
		return imgdata.json()
	})
	.then((imgdata)=>{
		if(imgdata.ok == 1)
		{
			imgdata = imgdata.info
		

			let img = document.createElement("img");
			img.setAttribute("src", imgdata)
			img.setAttribute("width", 200);
			img.setAttribute("heigth", 200);

			let chat_div = document.getElementById("chat");
			let d = document.createElement("div");
			d.appendChild(img);
			chat_div.appendChild(d);
		}
		if(imgdata.ok == 0)
		{
				console.log("image wasnt gotten");
		}
	})
	.catch((e)=>{
		console.log(e);
	})
}
function appendConvo(str, username, color)
{
	let chat_div = document.getElementById("chat");
	let d = document.createElement("div");
	d.setAttribute("style", `border-radius: 10px; background-color: ${color}; color: white; padding: 5px; margin-bottom: 10px;`)

	let username_el = document.createElement("div");
	username_el.innerHTML = username+':'
	username_el.setAttribute("style", "text-decoration: underline; margin-bottom: 10px;")
	let user_chat_text = document.createElement("div");
	user_chat_text.innerHTML = str
	d.appendChild(username_el);
	d.appendChild(user_chat_text);
	chat_div.appendChild(d);
}
document.getElementById('uploadForm').addEventListener('submit', async function(event) {
	event.preventDefault(); // Prevent the default form submission

	// Get the selected file from the input element
	const fileInput = document.getElementById('fileInput');
	const file = fileInput.files[0];

	if (file) {

		const formData = new FormData();
		formData.append('image', file);
		 
		const reader = new FileReader();

		reader.onload = async function(event) {
			const arrayBuffer = event.target.result;

			let url = new URL(window.location.href)
			let u = url.searchParams.get("u")
			if(u == undefined)
				u = "adm"
			//let base64 = arrayBufferToBase64(arrayBuffer);
			

			let json = {
				method: 'POST',
				headers: {
					'Content-Disposition': `attachment; filename="${file.name}"`,
					'Content-Length': arrayBuffer.length
				},
				body: arrayBuffer
			}
			
			// Use the fetch API to send the raw bytes to the server
			let res = await fetch(`http://${common.IP}:42069/sendimg?u=${u}`, json).then((e)=>{
					console.log(e)
			}).catch((e)=>{
					console.log(e)
			})
			console.log(res)
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
async function start()
{
	await common.insertHeader()

	let chat_div = document.getElementById("chat");
	chat_div.setAttribute("style", "position: relative; top: 100px; margin: auto; width: 40%;")
	let chat_area_div = document.getElementById("chat_tarea_div");
	chat_area_div.setAttribute("style", "position: fixed; top: 80%; left: 30%; width: 30%; height: 10%; z-index: 5")

	let url = new URL(window.location.href)
	let u = url.searchParams.get("u")
	if(u == undefined)
		u = "adm"

	let socket_img = common.createWSConnection((data)=>{
		appendConvoImg(data.data)
	}, "/img")
	let socket = common.createWSConnection((data)=>{
		appendConvo(data.data, u, "#222222")
	})
	// Connection opened


	let chat_sendbutton = document.getElementById("send_but");
	chat_sendbutton.addEventListener("click", (e)=>{
		let chat_tarea        = document.getElementById("chat_tarea");

		fetch(`http://${common.IP}:42069/send?to=${u}`,
		{
				method: "POST",
				body: chat_tarea.value
		})
		appendConvo(chat_tarea.value, "me", "#4444cc")
		chat_tarea.value = "";
	})
	//socket.send("whats up player?");
}

