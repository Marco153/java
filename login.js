import * as common from "./common.js"
window.onload = start

function start()
{
	common.insertHeader()
	let login = document.getElementById("login");
	let cad   = document.getElementById("cad");

	cad.addEventListener("click", async ()=>{
		let u = document.getElementById("name");
		let pw   = document.getElementById("pw");
		let res = await fetch(`${common.fetch_IP}/cad?u=${u.value}&pw=${pw.value}`)

		let json_res = await res.json()

		if(json_res.ok == 0)
		{
			let err   = document.getElementById("err");
			err.textContent = json_res.info;

		}
	})

}
