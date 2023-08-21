import { useRef, useEffect } from "react";
import { Niivue, NVImage } from "@niivue/niivue";

const nv = new Niivue();
export const NiiViewer = ({ imageUrl, frame }) => {
    const canvas = useRef();

    useEffect(() => {
        const volumeList = [
            {
                // url: "/patient119_4d.nii.gz",
                // url: "https://niivue.github.io/niivue-demo-images/mni152.nii.gz",
                url: "/api/v1/cardiac/download/patient102?test=patient102.nii.gz",
                name: "main.nii.gz",
                frame4D: 0
            },
            // {
            //     url: "/patient101_4D_seg.nii",
            //     frame4D: 0,
            //     colorMap: "cool"
            // },
        ];

        nv.attachToCanvas(canvas.current);
        nv.loadVolumes(volumeList);
    }, [imageUrl]);

    useEffect(() => {
        if(!nv.volumes[1]) return;

        const modFrame = frame % nv.volumes[1].nFrame4D;

        for(const volume of nv.volumes) {
            nv.setFrame4D(volume.id, modFrame);
        }

        console.log("frame", modFrame);
    }, [frame]);

    return <canvas ref={canvas} height={480} width={640} />;
};
