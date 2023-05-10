import Trigger from "./Trigger";

export default interface Log {
    id: number;
    message: number;
    type: string;
    date: Date;
    trigger: Trigger
}
