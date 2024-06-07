import * as common from "./common.js"
window.onload = start

function start()
{
	common.insertHeader()
	let login = document.getElementById("login");
	login.addEventListener("click", async ()=>{
		let u = document.getElementById("name");
		let pw   = document.getElementById("pw");
		let res = await fetch(`${common.fetch_IP}/auth?u=${u.value}&pw=${pw.value}`)

		let json_res = await res.json()

		if(json_res.ok == 0)
		{
			let err   = document.getElementById("err");
			err.textContent = json_res.info;

		}
		if(json_res.ok == 1)
		{
			sessionStorage.setItem("uid", json_res.info);

			window.location.href = "/userpanel";

			/*
			let err   = document.getElementById("err");
			err.textContent = json_res.info;
			let res = await fetch(`${common.fetch_IP}/userpanel`, {
				method: "POST",
				body: JSON.stringify({session: json_res.info})
			})

			let json = await res.json();
			document.write(json.info);
			*/


		}
	})
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
		if(json_res.ok == 1)
		{
			let err   = document.getElementById("err");
			err.textContent = json_res.info;

		}
	})

}
