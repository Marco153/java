window.onload = start
function insertItem(str)
{
	let el = document.createElement("div")
	let text = document.createTextNode(str);
	el.appendChild(text);

	document.getElementById("products").appendChild(el);

}
function start()
{
	insertItem("aoe");
	insertItem("324");
}
