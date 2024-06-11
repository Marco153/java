import * as common from "./common.js"
window.onload = start

async function start()
{
	await common.insertHeader()
	await common.insertNewButtonInHeader("fale conosco", "/chat");

	common.createWSConnection(()=>{})
}
