import {useState} from "react";
import {FileUploader} from "react-drag-drop-files";
import {IconButton, InputBase, LinearProgress, Paper, Typography} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';
import axios from "axios";

const UploadState = {
    None: "None",
    Uploading: "Uploading",
    Segmentation: "Segmentation",
    FeatureExtraction: "FeatureExtraction",
    Classification: "Classification"
}

function Main() {
    const [uploadState, setUploadState] = useState(UploadState.None);

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


    };

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

                                    placeholder="Search patient number"
                                    inputProps={{ 'aria-label': 'Search patient number' }}
                                />
                                <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
                                    <SearchIcon style={{ color: 'white' }}  />
                                </IconButton>
                            </Paper>

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
