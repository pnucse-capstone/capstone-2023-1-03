import React, {useEffect, useState} from "react";
import {FileUploader} from "react-drag-drop-files";
import {
    Card, FormControl,
    IconButton,
    InputBase,
    InputLabel,
    LinearProgress, List, ListItem, ListItemText, MenuItem,
    Paper, Select,
    TextField,
    Typography
} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';
import axios from "axios";
import {ViewInAr} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";


const UploadState = {
    None: "None",
    Uploading: "Uploading",
    Segmentation: "Segmentation",
    FeatureExtraction: "FeatureExtraction",
    Classification: "Classification"
}

function Main() {
    const navigate = useNavigate();

    const [uploadState, setUploadState] = useState(UploadState.None);
    const [data, setData] = useState([]);
    const [leftOperand, setLeftOperand] = useState("");
    const [operator, setOperator] = useState(">");
    const [rightOperand, setRightOperand] = useState("");
    const [searchText, setSearchText] = useState("");

    const getData = async () => {
        const res = await axios.get("/api/v1/data");

        for(const d of res.data) {
            d.ef_LV = (d.ef_LV * 100).toFixed(1);
            d.ef_RV = (d.ef_RV * 100).toFixed(1);
            d.ed_vol_LV = (d.ed_vol_LV).toFixed(1);
            d.ed_vol_RV = (d.ed_vol_RV).toFixed(1);
            d.ed_max_MTH = (d.ed_max_MTH).toFixed(1);
        }

        setData(res.data);

        console.log("res.data", res.data)
    }

    useEffect(() => {
        getData();
    }, [])

    const handleChange = async (file) => {
        setUploadState(UploadState.Uploading);

        const formData = new FormData();

        formData.append("patientZip", file);

        const res = await axios.post("/api/v1/cardiac/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
            transformRequest: [
                function () {
                    return formData;
                },
            ],
        });

        const patientNumber = res.data;

        setUploadState(UploadState.Segmentation);

        await axios.post("/api/v1/cardiac/segmentation/" + patientNumber);

        setUploadState(UploadState.FeatureExtraction);

        await axios.post("/api/v1/cardiac/feature-extraction/" + patientNumber);

        setUploadState(UploadState.Classification);

        await axios.post("/api/v1/cardiac/classification/" + patientNumber);

        navigate(`/viewer/${patientNumber}`);
    };

    const patientWithSearch = data.map((d) => {
        if(d.name.includes(searchText) || d.diseaseGroup.includes(searchText))
            return d;

        return null;
    });

    const patientWithCondition = data.map((d) => {
         if(operator === ">" && parseFloat(d[leftOperand]) >= parseFloat(rightOperand))
             return d;

        if(operator === "<" && parseFloat(d[leftOperand]) <= parseFloat(rightOperand))
            return d;

        return null;
    });

    const listItemsWithSearch = patientWithSearch.map((d) => {
        if(d === null)
            return <div/>;

        return(
            <Card style={{marginBottom: 5, background:"#2a2d3a"}}>
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

    const listItems = patientWithCondition.map((d) => {
        if(d === null)
            return <div/>;

        let conditionText = "";

        if(leftOperand === "ed_vol_LV")
            conditionText = `LV vol: ${d.ed_vol_LV}`;

        if(leftOperand === "ed_vol_RV")
            conditionText = `RV vol: ${d.ed_vol_RV}`;

        if(leftOperand === "ef_LV")
            conditionText = `LV ejection fraction: ${d.ef_LV}`;

        if(leftOperand === "ef_RV")
            conditionText = `RV ejection fraction: ${d.ef_RV}`;

        if(leftOperand === "ed_max_MTH")
            conditionText = `MYO thickness: ${d.ed_max_MTH}`;

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
                    secondary={`group: ${d.diseaseGroup}, ${conditionText}`}
                />
            </ListItem>
            </Card>
        )});

    return (
        <div className="Main" style={{height: "100vh"}}>
            <div style={{display:'flex', justifyContent:'center', alignItems:'center'}}>
                <div style={{marginTop: 100, textAlign: "center"}}>
                    {/*title*/}
                    <Typography fontWeight={800} fontSize={50} >Cardiac Classifier</Typography>

                    {uploadState === UploadState.None ?
                        <div>
                            {/*search bar*/}
                            <Paper
                                style={{marginTop: 20}}
                                component="form"
                                sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 500,
                                    "& input": {
                                        color: 'white',
                                    }
                                }}
                            >
                                <InputBase
                                    sx={{ ml: 1, flex: 1 }}

                                    placeholder="Search patient number or disease group"
                                    inputProps={{ 'aria-label': 'Search patient number' }}
                                    onChange={(e) => setSearchText(e.target.value)}
                                />
                                <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
                                    <SearchIcon style={{ color: 'white' }} />
                                </IconButton>
                            </Paper>

                            <div style={{display: "flex", marginTop: 20}}>
                                <FormControl style={{width: 270, marginRight: 10}}>
                                    <InputLabel id="left-operand-label">Search with condition</InputLabel>
                                    <Select
                                        labelId="left-operand-label"
                                        id="left-operand-select"
                                        value={leftOperand}
                                        label="Search with condition"
                                        onChange={(e) => setLeftOperand(e.target.value)}
                                    >
                                        <MenuItem value={"ed_vol_LV"}>LV volume when ED (mL/m<sup>2</sup>)</MenuItem>
                                        <MenuItem value={"ed_vol_RV"}>RV volume when ED (mL/m<sup>2</sup>)</MenuItem>
                                        <MenuItem value={"ef_LV"}>LV ejection fraction (%)</MenuItem>
                                        <MenuItem value={"ef_RV"}>RV ejection fraction (%)</MenuItem>
                                        <MenuItem value={"ed_max_MTH"}>MYO thickness (mm)</MenuItem>
                                    </Select>
                                </FormControl>

                                <Select
                                    style={{marginRight: 10, width: 140}}
                                    id="operator"
                                    value={operator}
                                    onChange={(e) => setOperator(e.target.value)}
                                >
                                    <MenuItem value={">"}>{"greater then"}</MenuItem>
                                    <MenuItem value={"<"}>{"less then"}</MenuItem>
                                </Select>

                                <TextField sx={{
                                    width: { sm: 70, md: 70 },
                                    "& .MuiInputBase-root": {
                                        height: 60
                                    }
                                }} value={rightOperand} onChange={(e) => setRightOperand(e.target.value)}></TextField>
                            </div>

                            {searchText.length > 0 &&
                                <List>
                                    {listItemsWithSearch}
                                </List>
                            }

                            {rightOperand.length > 0 &&
                                <List>
                                    {listItems}
                                </List>
                            }

                            <div style={{marginTop: 20}}>
                                <FileUploader handleChange={handleChange} name="file" types={["ZIP"]} />
                            </div>
                        </div>
                        :
                        <div style={{marginTop: 150}}>
                            <Typography fontWeight={800} style={{marginBottom: 20}}>{uploadState}</Typography>
                            <LinearProgress></LinearProgress>
                        </div>
                    }
                </div>
            </div>
        </div>
    );
}

export default Main;
