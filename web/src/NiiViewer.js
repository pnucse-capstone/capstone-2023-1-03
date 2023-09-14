import { useRef, useEffect } from "react";
import { Niivue, NVImage } from "@niivue/niivue";

const nv = new Niivue();
export const NiiViewer = ({ patientNumber, frame, class1Opacity, class2Opacity, class3Opacity }) => {
    const canvas = useRef();

    useEffect(() => {
        const volumeList = [
            {
                url: `/api/v1/cardiac/download-origin/${patientNumber}`,
                name: "main.nii.gz",
                frame4D: 0
            },
            { //RV
                url: `/api/v1/cardiac/download-segmentation/${patientNumber}/class1`,
                frame4D: 0,
                name: "class1.nii",
                colorMap: "red",
                cal_max: 3,
                opacity: 0
            },
            { //MYO
                url: `/api/v1/cardiac/download-segmentation/${patientNumber}/class2`,
                frame4D: 0,
                name: "class2.nii",
                colorMap: "green",
                cal_max: 3,
                opacity: 0
            },
            { //LV
                url: `/api/v1/cardiac/download-segmentation/${patientNumber}/class3`,
                frame4D: 0,
                name: "class3.nii",
                colorMap: "blue",
                cal_max: 3,
                opacity: 0
            },
        ];

        nv.attachToCanvas(canvas.current);
        nv.loadVolumes(volumeList);
    }, [patientNumber]);

    useEffect(() => {
        if(!nv.volumes[1]) return;

        const modFrame = frame % nv.volumes[1].nFrame4D;

        for(const volume of nv.volumes) {
            nv.setFrame4D(volume.id, modFrame);
        }

        console.log("frame", modFrame);
    }, [frame]);

    useEffect(() => {
        if(!nv.volumes[1]) return;

        nv.setOpacity(1, class1Opacity);
    }, [class1Opacity]);

    useEffect(() => {
        if(!nv.volumes[2]) return;

        nv.setOpacity(2, class2Opacity);
    }, [class2Opacity]);

    useEffect(() => {
        if(!nv.volumes[3]) return;

        nv.setOpacity(3, class3Opacity);
    }, [class3Opacity]);

    return <canvas ref={canvas}/>;
};
