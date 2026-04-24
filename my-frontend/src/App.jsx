import { useState } from "react";

function App() {
    const [title, setTitle] = useState("Default value");

    return (
        <>
            <h1>React + {title}</h1>
        </>
    );
}

export default App;