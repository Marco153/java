import * as common from "./common.js"
window.onload = start

function addUser(name)
{
	let d = document.createElement("div")
	d.addEventListener("click", ()=>{
		window.location.href = "/chat?u="+name
	})
	let t = document.createTextNode(name)
	d.appendChild(t)
	return d
}
async function start()
{
	await common.insertHeader()

	common.createWSConnection(()=>{})

	let res = await fetch(`${common.fetch_IP}/usersinfo?tb=users`)
	let json = await res.json();

	let page = document.getElementById("page")

	let db = json.db;
	for(let i = 0; i < db.length; i++)
	{
		let u = db[i]
		
		page.appendChild(addUser(u.name))

	}
}
