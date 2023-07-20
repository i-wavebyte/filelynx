import {Injectable} from "@angular/core";
import swal from 'sweetalert2';
import { DossierService } from "./dossier.service";
import { NgToastService } from "ng-angular-popup";
import { Router } from "@angular/router";
import { CompagnieService } from "./compagnie.service";

@Injectable()
export class HelperService {

    constructor(private dossierService: DossierService , private compagnieService: CompagnieService, private toast: NgToastService,
        private router:Router) { }

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

    // show(title: string, message: string, username: string, folderId: number, id: number) {
    //     swal.fire({
    //         title: title,
    //         text: message,
    //         icon: 'warning',
    //         showCancelButton: true,
    //         confirmButtonText: 'Supprimer',
    //         reverseButtons: true,
    //     }).then((result) => {
    //         if (result.value)
    //         {
    //             if (id ==0)
    //             {
    //                 // Call your backend service endpoint here
    //                 this.dossierService.deleteFolder(folderId).subscribe(
    //                     (response) => {
    //                         console.log(response.message);
    //                         this.toast.success({detail:"Message de réussite", summary: response.message, duration: 3000});
    //                         console.log(response);
    //                         // Handle success response
    //                         console.log('Data deleted successfully');
    //                         // this.router.navigate(['/files']);
    //                     }, 
    //                     (err) => {
    //                         console.log(err);
    //                         this.toast.error({detail:"Message d'erreur", summary:err.error.message, duration:3000});
    //                     });
    //             }
    //             else if(id ==1)
    //             {
    //                 console.log("hna");
    //                 this.compagnieService.deleteMembre(folderId, username).subscribe((response) => {
    //                     this.toast.success({detail:"Message de réussite", summary: response.message, duration: 3000});
    //                     console.log(response);
    //                     // this.loadMembres();
    //                   },
    //                   (err) => {
    //                     this.toast.error({detail:"Message d'erreur", summary:"Erreur lors de la tentative de suppression du membre", duration: 3000});
    //                   })
    //             }
    //         }
    //     });
    // }

    show(title: string, message: string, username: string, folderId: number, id: number, ): Promise<number> {
        return new Promise<number>((resolve) => {
          swal.fire({
            title: title,
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Supprimer',
            reverseButtons: true,
          }).then((result) => {
            if (result.value) {
              if (id == 0) {
                this.dossierService.deleteFolder(folderId).subscribe(
                  (response) => {
                    console.log(response.message);
                    this.toast.success({ detail: "Message de réussite", summary: response.message, duration: 3000 });
                    console.log(response);
                    console.log('Data deleted successfully');
                    resolve(0); // Resolve with 0 for folder deletion
                  },
                  (err) => {
                    console.log(err);
                    this.toast.error({ detail: "Message d'erreur", summary: err.error.message, duration: 3000 });
                    resolve(-1); // Resolve with -1 for error
                  });
              } else if (id == 1) {
                console.log("hna");
                this.compagnieService.deleteMembre(folderId, username).subscribe(
                  (response) => {
                    this.toast.success({ detail: "Message de réussite", summary: response.message, duration: 3000 });
                    console.log(response);
                    resolve(0); // Resolve with 1 for member deletion
                  },
                  (err) => {
                    this.toast.error({ detail: "Message d'erreur", summary: "Erreur lors de la tentative de suppression du membre", duration: 3000 });
                    resolve(-1); // Resolve with -1 for error
                  });
              }
            } else {
              resolve(-2); // Resolve with -2 for cancellation
            }
          });
        });
      }
      
}