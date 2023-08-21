import './App.css';
import {NiiViewer} from './NiiViewer';
import {useState} from "react";
import axios from "axios";

let isInit = false;

function blobToDataUrl(blob) {
    return new Promise(r => {let a=new FileReader(); a.onload=r; a.readAsDataURL(blob)}).then(e => e.target.result);
}

function App() {
    const [frame, setFrame] = useState(0);
    const [data, setData] = useState(undefined);
    let timer = null;

    const test = async () => {
        const res1 = await fetch("https://niivue.github.io/niivue-demo-images/mni152.nii.gz")

        console.log("res git", res1.arrayBuffer());

        const res2 = await fetch("/api/v1/cardiac/download/patient102");

        console.log("res localserver", res2.arrayBuffer());

       // const dataURL = await blobToDataUrl(res.data);
       //
       // console.log(dataURL);
       //
       //  setData(dataURL);
    }

    if(!isInit) {
        test();
        isInit = true;
    }


  return (
    <div className="App">
      <NiiViewer imageUrl={data} frame={frame}/>
        <button onClick={() => {
            timer = setInterval(() => {
                setFrame((prevFrame) => prevFrame + 1);
            }, 10)}}>anim start
        </button>

        <button onClick={() => {
            clearInterval(timer);

            timer = null;
        }}>anim start
        </button>
    </div>
  );
}

export default App;
