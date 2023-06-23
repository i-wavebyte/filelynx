import {Injectable} from "@angular/core";
import swal from 'sweetalert2';
import { DossierService } from "./dossier.service";
import { NgToastService } from "ng-angular-popup";

@Injectable()
export class HelperService {

    constructor(private dossierService: DossierService , private toast: NgToastService
    ) { }

    showLoading() {
        swal.showLoading();
    }

    hideLoading() {
        swal.close();
    }

    alert(title: string, message: string, type: any) {
        return swal.fire({
            title: title,
            text: message,
            icon: type
        });
    }

    htmlAlert(title: string, message: string, type: any) {
        return swal.fire({
            title: title,
            html: message,
            icon: type
        });
    }

    confirm(title: string, message: string, cb: any) {
        swal.fire({
            title: title,
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Confirmer',
            cancelButtonText: 'Annuler',
            reverseButtons: true
        }).then((result) => {
            if (result.value) {
                cb();
            }
        });
    }

    show(title: string, message: string, folderId: number) {
        swal.fire({
            title: title,
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Supprimer',
            reverseButtons: true,
        }).then((result) => {
            if (result.value)
            {
                // Call your backend service endpoint here
                this.dossierService.deleteFolder(folderId).subscribe(
                (response) => {
                    console.log(response.message);
                    this.toast.success({detail:"Message de réussite", summary: response.message, duration: 3000})

                    // Handle success response
                    console.log('Data deleted successfully');
                }, 
                (err) => {
                    console.log(err);
                    this.toast.error({detail:"Message d'erreur", summary:err.error.message, duration:3000});
                }) 
            }
        });
    }
}