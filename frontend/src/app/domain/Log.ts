import Trigger from "./Trigger";

export default interface Log {
    id: number;
    message: String;
    type: string;
    date: Date;
    trigger: Trigger
}
