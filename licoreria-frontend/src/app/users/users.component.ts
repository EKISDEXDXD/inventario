import { Component, OnInit } from "@angular/core";

interface User {
    id: number;
    name: string;
}

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
    
    constructor() { }
    
    ngOnInit(): void {
    }
}