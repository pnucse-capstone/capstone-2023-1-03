import {useNavigate, useParams} from "react-router-dom";
import {NiiViewer} from "../NiiViewer";
import React, {useEffect, useState} from "react";
import {Card, CardContent, IconButton, List, ListItem, ListItemText, Slider, Switch, Typography} from "@mui/material";

import axios from "axios";
import {ViewInAr} from "@mui/icons-material";

function CardiacViewer() {
    const navigate = useNavigate();
    let { patientNumber} = useParams();

    const [frame, setFrame] = useState(1);
    const [enableSegmentation, setEnableSegmentation] = useState(false);
    const [class1Opacity, setClass1Opacity] = useState(1);
    const [class2Opacity, setClass2Opacity] = useState(1);
    const [class3Opacity, setClass3Opacity] = useState(1);

    const [patientInfo, setPatientInfo] = useState(null);
    const [classificationInfo, setClassificationInfo] = useState(null);

    const [data, setData] = useState([]);

    const getData = async () => {
        const res = await axios.get("/api/v1/data");

        setData(res.data);

        console.log("res.data", res.data)
    }

    const getInfoFile = async () => {
        const res = await axios.get(`/api/v1/cardiac/download-info/${patientNumber}`)

        const infoStr = res.data.split("\n");

        const info = {
            ED: infoStr[0].split(": ")[1],
            ES: infoStr[1].split(": ")[1],
            height: infoStr[3].split(": ")[1],
            NbFrame: infoStr[4].split(": ")[1],
            weight: infoStr[5].split(": ")[1],
        }

        console.log(info);

        setPatientInfo(info);
    }

    const getClassificationInfo = async () => {
        const res = await axios.get(`/api/v1/cardiac/classification-info/${patientNumber}`)

        console.log(res.data);

        setClassificationInfo(res.data);
    }

    useEffect( () => {
        getInfoFile();
        getClassificationInfo();
        getData();
    }, [patientNumber]);

    const info = classificationInfo;

    const patientWithCondition = data.map((d) => {
        if(d.diseaseGroup === classificationInfo.diseaseGroup)
            return d;

        return null;
    });

    const listItems = patientWithCondition.map((d) => {
        if(d === null)
            return;

        return(
            <Card style={{marginBottom: 5}}>
                <ListItem
                    secondaryAction={
                        <IconButton edge="end" aria-label="view" onClick={() => {
                            navigate(`/viewer/${d.name}`);
                        }}>
                            <ViewInAr />
                        </IconButton>
                    }
                >
                    <ListItemText
                        primary={d.name}
                        secondary={`group: ${d.diseaseGroup}`}
                    />
                </ListItem>
            </Card>
        )});

    return (
        <div style={{height: "100vh", display: "flex"}}>
            <div style={{width: "70vw", height: "100vh"}}>
                <NiiViewer patientNumber={patientNumber}
                           frame={frame - 1}
                           class1Opacity={enableSegmentation ? class1Opacity : 0}
                           class2Opacity={enableSegmentation ? class2Opacity : 0}
                           class3Opacity={enableSegmentation ? class3Opacity : 0}
                />
            </div>

            {patientInfo &&
            <div style={{width: "15vw", paddingTop: 10, paddingLeft: 5}}>
                <Card>
                    <CardContent>
                        <Typography variant="h6" component="h2">
                            Patient Info
                        </Typography>
                        <Typography>
                            ED : {patientInfo.ED}
                        </Typography>
                        <Typography>
                            ES : {patientInfo.ES}
                        </Typography>
                        <Typography>
                            Height : {patientInfo.height}
                        </Typography>
                        <Typography>
                            Weight : {patientInfo.weight}
                        </Typography>
                    </CardContent>
                </Card>

                <Card style={{marginTop: 10}}>
                    <CardContent>
                        <Typography variant="h6" component="h2">
                            Viewer Setting
                        </Typography>

                        <Typography style={{marginTop: 5}}>Enable Segmentation</Typography>

                        <div style={{display:"flex", justifyContent: "space-between"}}>
                            {enableSegmentation ?
                                <Typography>Enabled</Typography>
                                :
                                <Typography>Disabled</Typography>
                            }

                            <Switch
                                checked={enableSegmentation}
                                onChange={(event) => setEnableSegmentation(event.target.checked)}
                                inputProps={{ 'aria-label': 'controlled' }}
                            />
                        </div>

                        {enableSegmentation &&
                        <div>
                            <Typography style={{marginTop: 5}}>Change opacity</Typography>
                            <div style={{display:"flex", justifyContent: "space-between"}}>
                                <Typography>RV:</Typography>
                                <Slider
                                    style={{width: "5vw"}}
                                    min={0}
                                    max={1}
                                    step={0.01}
                                    aria-label="Default"
                                    valueLabelDisplay="auto"
                                    value={class1Opacity}
                                    onChange={(e, val) => setClass1Opacity(val)}
                                />
                            </div>

                            <div style={{display:"flex", justifyContent: "space-between"}}>
                                <Typography>MYO:</Typography>
                                <Slider style={{width: "5vw"}}
                                        min={0}
                                        max={1}
                                        step={0.01}
                                        aria-label="Default"
                                        valueLabelDisplay="auto"
                                        value={class2Opacity}
                                        onChange={(e, val) => setClass2Opacity(val)}
                                />
                            </div>

                            <div style={{display:"flex", justifyContent: "space-between"}}>
                                <Typography>LV:</Typography>
                                <Slider style={{width: "5vw"}}
                                        min={0}
                                        max={1}
                                        step={0.01}
                                        aria-label="Default"
                                        valueLabelDisplay="auto"
                                        value={class3Opacity}
                                        onChange={(e, val) => setClass3Opacity(val)}
                                />
                            </div>
                        </div>
                        }
                        <Typography style={{marginTop: 10}}>Change MRI frame</Typography>
                        {patientInfo &&
                            <div style={{display:"flex", justifyContent: "space-between"}}>
                                <Typography>Frame: </Typography>
                                <Slider style={{width: "5vw"}}
                                        min={1}
                                        max={parseInt(patientInfo.NbFrame) + 1}
                                        aria-label="Default"
                                        valueLabelDisplay="auto"
                                        value={frame}
                                        onChange={(e, val) => setFrame(val)}
                                />
                            </div>
                        }
                    </CardContent>
                </Card>
                {info &&
                    <Card style={{marginTop: 10}}>
                        <CardContent>
                            <Typography variant="h6" component="h2">
                                Classification Info
                            </Typography>

                            <Typography>
                                Disease Type: {info.diseaseGroup}
                            </Typography>

                            <Typography style={{marginTop: 10}} variant="h6" component="h2">
                                Reason
                            </Typography>
                            {info.diseaseGroup === "NOR" &&
                            <div>
                                <Typography>
                                    LV (EF): <span style={{color: info.ef_LV > 0.5 ? "#c7ffa1" : "#ffa7a7"}}>{(info.ef_LV * 100).toFixed(1)}%</span> ({">"} 50%)
                                </Typography>
                                <Typography>
                                    ED LV (Vol): <span style={{color: info.ed_vol_LV < 90 ? "#c7ffa1" : "#ffa7a7"}}>{(info.ed_vol_LV).toFixed(1)}mL/m<sup>2</sup></span> ({"<"} 90mL/m<sup>2</sup>)
                                </Typography>
                                <Typography>
                                    RV (EF): <span style={{color: info.ef_RV > 0.4 ? "#c7ffa1" : "#ffa7a7"}}>{(info.ef_RV * 100).toFixed(1)}%</span> ({">"} 40%)
                                </Typography>
                                <Typography>
                                    ED RV (Vol): <span style={{color: info.ed_vol_RV < 100 ? "#c7ffa1" : "#ffa7a7"}}>{(info.ed_vol_RV).toFixed(1)}mL/m<sup>2</sup></span> ({"<"} 100mL/m<sup>2</sup>)
                                </Typography>
                                <Typography>
                                    ED MYO: <span style={{color: info.ed_max_MTH < 12 ? "#c7ffa1" : "#ffa7a7"}}>{(info.ed_max_MTH).toFixed(1)}mm</span> ({"<"} 12mm)
                                </Typography>
                            </div>
                            }
                            {info.diseaseGroup === "MINF" &&
                            <div>
                                <Typography>
                                    LV (EF): <span style={{color: info.ef_LV < 0.4 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ef_LV * 100).toFixed(1)}%</span> ({"<"} 40%)
                                </Typography>
                                <Typography>
                                    ED LV (Vol): <span style={{color: info.ed_vol_LV > 80 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ed_vol_LV).toFixed(1)}mL/m<sup>2</sup></span> ({">"} 80mL/m<sup>2</sup>)
                                </Typography>
                            </div>
                            }
                            {info.diseaseGroup === "DCM" &&
                            <div>
                                <Typography>
                                    LV (EF): <span style={{color: info.ef_LV < 0.4 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ef_LV * 100).toFixed(1)}%</span> ({"<"} 40%)
                                </Typography>
                                <Typography>
                                    ED LV (Vol): <span style={{color: info.ed_vol_LV > 100 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ed_vol_LV).toFixed(1)}mL/m<sup>2</sup></span> ({">"} 100mL/m<sup>2</sup>)
                                </Typography>
                                <Typography>
                                    ED RV (Vol): <span style={{color: info.ed_vol_RV > 100 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ed_vol_RV).toFixed(1)}mL/m<sup>2</sup></span> ({">"} 100mL/m<sup>2</sup>)
                                </Typography>
                            </div>
                            }
                            {info.diseaseGroup === "HCM" &&
                                <div>
                                    <Typography>
                                        ED MYO: <span style={{color: info.ed_max_MTH > 15 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ed_max_MTH).toFixed(1)}mm</span> ({">"} 15mm)
                                    </Typography>
                                </div>
                            }
                            {info.diseaseGroup === "RV" &&
                                <div>
                                    <Typography>
                                        RV (EF): <span style={{color: info.ef_RV < 0.4 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ef_RV * 100).toFixed(1)}%</span> ({"<"} 40%)
                                    </Typography>
                                    <Typography>
                                        ED RV (Vol): <span style={{color: info.ed_vol_RV > 100 ? "#ffa7a7" : "#c7ffa1"}}>{(info.ed_vol_RV).toFixed(1)}mL/m<sup>2</sup></span> ({">"} 100mL/m<sup>2</sup>)
                                    </Typography>
                                </div>
                            }
                        </CardContent>
                    </Card>
                }
            </div>
            }
            <div style={{width: "15vw", paddingTop: 10, paddingLeft: 5, paddingRight: 5, height: "100vh", overflow: "scroll"}}>
                <Typography>Patient with the same disease group</Typography>
                <List>
                    {listItems}
                </List>
            </div>
        </div>
    )
}

export default CardiacViewer;