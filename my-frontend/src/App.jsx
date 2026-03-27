import React from "react";
import { useEffect, useState } from "react";

// A React Component is just a function that returns a UI.
export default function App() { //Defines React Component called App

    const [data, setData] = useState(null); //Data will eventually hold the JSON i get back from Backend.
    const [err, setErr] = useState(null); // err Holds an error message if fetch fails.

    useEffect(() => {
        fetch("/api/form") //run the fetch when component loads.
            .then((res) => { //res is the HTTP response object.
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.json();
            })
            .then((json) => {
                console.log("GET /api/form:", json);
                setData(json);
            })
            .catch((e) => {
                console.error("Fetch failed:", e);
                setErr(String(e));
            });
    }, []);

    return (
        <div className="container mt-5">
            <h1>Backend GET Debug</h1>

            {err && <div className="alert alert-danger">Error: {err}</div>}

            <pre style={{ background: "#f5f5f5", padding: "1rem" }}>
        {data ? JSON.stringify(data, null, 2) : "Loading..."}
      </pre>
        </div>
    );
}