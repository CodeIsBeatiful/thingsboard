System.register(["@angular/core","@shared/public-api","@angular/forms","@ngrx/store","@angular/material/form-field","@angular/flex-layout/flex","@ngx-translate/core","@angular/material/input","@angular/common","@angular/cdk/keycodes","@angular/material/chips","@angular/material/icon","@home/components/public-api"],(function(t){"use strict";var e,o,r,a,n,s,m,l,i,u,p,c,f,y,d,g,x,C,L,h;return{setters:[function(t){e=t,o=t.Component,r=t.NgModule},function(t){a=t.RuleNodeConfigurationComponent,n=t.SharedModule},function(t){s=t.Validators,m=t},function(t){l=t},function(t){i=t},function(t){u=t},function(t){p=t},function(t){c=t},function(t){f=t.CommonModule,y=t},function(t){d=t.ENTER,g=t.COMMA,x=t.SEMICOLON},function(t){C=t},function(t){L=t},function(t){h=t.HomeComponentsModule}],execute:function(){class b extends a{constructor(t,e){super(t),this.store=t,this.fb=e}configForm(){return this.getSumIntoMetadataConfigForm}onConfigurationSet(t){this.getSumIntoMetadataConfigForm=this.fb.group({inputKey:[t?t.inputKey:null,[s.required]],outputKey:[t?t.outputKey:null,[s.required]]})}}t("GetSumIntoMetadataConfigComponent",b),b.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:b,deps:[{token:l.Store},{token:m.FormBuilder}],target:e.ɵɵFactoryTarget.Component}),b.ɵcmp=e.ɵɵngDeclareComponent({minVersion:"12.0.0",version:"12.2.15",type:b,selector:"tb-enrichment-node-sum-into-metadata-config",usesInheritance:!0,ngImport:e,template:'<section [formGroup]="getSumIntoMetadataConfigForm" fxLayout="column">\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.input-key</mat-label>\n        <input matInput formControlName="inputKey" required>\n    </mat-form-field>\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.output-key</mat-label>\n        <input matInput formControlName="outputKey" required>\n    </mat-form-field>\n</section>\n',components:[{type:i.MatFormField,selector:"mat-form-field",inputs:["color","floatLabel","appearance","hideRequiredMarker","hintLabel"],exportAs:["matFormField"]}],directives:[{type:u.DefaultLayoutDirective,selector:"  [fxLayout], [fxLayout.xs], [fxLayout.sm], [fxLayout.md],  [fxLayout.lg], [fxLayout.xl], [fxLayout.lt-sm], [fxLayout.lt-md],  [fxLayout.lt-lg], [fxLayout.lt-xl], [fxLayout.gt-xs], [fxLayout.gt-sm],  [fxLayout.gt-md], [fxLayout.gt-lg]",inputs:["fxLayout","fxLayout.xs","fxLayout.sm","fxLayout.md","fxLayout.lg","fxLayout.xl","fxLayout.lt-sm","fxLayout.lt-md","fxLayout.lt-lg","fxLayout.lt-xl","fxLayout.gt-xs","fxLayout.gt-sm","fxLayout.gt-md","fxLayout.gt-lg"]},{type:m.NgControlStatusGroup,selector:"[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]"},{type:m.FormGroupDirective,selector:"[formGroup]",inputs:["formGroup"],outputs:["ngSubmit"],exportAs:["ngForm"]},{type:i.MatLabel,selector:"mat-label"},{type:p.TranslateDirective,selector:"[translate],[ngx-translate]",inputs:["translate","translateParams"]},{type:c.MatInput,selector:"input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]",inputs:["id","disabled","required","type","value","readonly","placeholder","errorStateMatcher","aria-describedby"],exportAs:["matInput"]},{type:m.DefaultValueAccessor,selector:"input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]"},{type:m.NgControlStatus,selector:"[formControlName],[ngModel],[formControl]"},{type:m.FormControlName,selector:"[formControlName]",inputs:["disabled","formControlName","ngModel"],outputs:["ngModelChange"]},{type:m.RequiredValidator,selector:":not([type=checkbox])[required][formControlName],:not([type=checkbox])[required][formControl],:not([type=checkbox])[required][ngModel]",inputs:["required"]}]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:b,decorators:[{type:o,args:[{selector:"tb-enrichment-node-sum-into-metadata-config",templateUrl:"./get-sum-into-metadata-config.component.html",styleUrls:[]}]}],ctorParameters:function(){return[{type:l.Store},{type:m.FormBuilder}]}});class N{}t("CustomNodesConfigEnrichmentModule",N),N.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:N,deps:[],target:e.ɵɵFactoryTarget.NgModule}),N.ɵmod=e.ɵɵngDeclareNgModule({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:N,declarations:[b],imports:[f,n],exports:[b]}),N.ɵinj=e.ɵɵngDeclareInjector({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:N,imports:[[f,n]]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:N,decorators:[{type:r,args:[{declarations:[b],imports:[f,n],exports:[b]}]}]});class F extends a{constructor(t,e){super(t),this.store=t,this.fb=e}configForm(){return this.checkKeyConfigForm}onConfigurationSet(t){this.checkKeyConfigForm=this.fb.group({key:[t?t.key:null,[s.required]]})}}t("CheckKeyConfigComponent",F),F.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:F,deps:[{token:l.Store},{token:m.FormBuilder}],target:e.ɵɵFactoryTarget.Component}),F.ɵcmp=e.ɵɵngDeclareComponent({minVersion:"12.0.0",version:"12.2.15",type:F,selector:"tb-filter-node-check-key-config",usesInheritance:!0,ngImport:e,template:'<section [formGroup]="checkKeyConfigForm" fxLayout="column">\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.msg-key</mat-label>\n        <input matInput formControlName="key" required>\n    </mat-form-field>\n</section>\n',components:[{type:i.MatFormField,selector:"mat-form-field",inputs:["color","floatLabel","appearance","hideRequiredMarker","hintLabel"],exportAs:["matFormField"]}],directives:[{type:u.DefaultLayoutDirective,selector:"  [fxLayout], [fxLayout.xs], [fxLayout.sm], [fxLayout.md],  [fxLayout.lg], [fxLayout.xl], [fxLayout.lt-sm], [fxLayout.lt-md],  [fxLayout.lt-lg], [fxLayout.lt-xl], [fxLayout.gt-xs], [fxLayout.gt-sm],  [fxLayout.gt-md], [fxLayout.gt-lg]",inputs:["fxLayout","fxLayout.xs","fxLayout.sm","fxLayout.md","fxLayout.lg","fxLayout.xl","fxLayout.lt-sm","fxLayout.lt-md","fxLayout.lt-lg","fxLayout.lt-xl","fxLayout.gt-xs","fxLayout.gt-sm","fxLayout.gt-md","fxLayout.gt-lg"]},{type:m.NgControlStatusGroup,selector:"[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]"},{type:m.FormGroupDirective,selector:"[formGroup]",inputs:["formGroup"],outputs:["ngSubmit"],exportAs:["ngForm"]},{type:i.MatLabel,selector:"mat-label"},{type:p.TranslateDirective,selector:"[translate],[ngx-translate]",inputs:["translate","translateParams"]},{type:c.MatInput,selector:"input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]",inputs:["id","disabled","required","type","value","readonly","placeholder","errorStateMatcher","aria-describedby"],exportAs:["matInput"]},{type:m.DefaultValueAccessor,selector:"input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]"},{type:m.NgControlStatus,selector:"[formControlName],[ngModel],[formControl]"},{type:m.FormControlName,selector:"[formControlName]",inputs:["disabled","formControlName","ngModel"],outputs:["ngModelChange"]},{type:m.RequiredValidator,selector:":not([type=checkbox])[required][formControlName],:not([type=checkbox])[required][formControl],:not([type=checkbox])[required][ngModel]",inputs:["required"]}]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:F,decorators:[{type:o,args:[{selector:"tb-filter-node-check-key-config",templateUrl:"./check-key-config.component.html",styleUrls:[]}]}],ctorParameters:function(){return[{type:l.Store},{type:m.FormBuilder}]}});class M{}t("CustomNodesConfigFilterModule",M),M.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:M,deps:[],target:e.ɵɵFactoryTarget.NgModule}),M.ɵmod=e.ɵɵngDeclareNgModule({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:M,declarations:[F],imports:[f,n],exports:[F]}),M.ɵinj=e.ɵɵngDeclareInjector({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:M,imports:[[f,n]]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:M,decorators:[{type:r,args:[{declarations:[F],imports:[f,n],exports:[F]}]}]});class I extends a{constructor(t,e){super(t),this.store=t,this.fb=e}configForm(){return this.getSumConfigForm}onConfigurationSet(t){this.getSumConfigForm=this.fb.group({inputKey:[t?t.inputKey:null,[s.required]],outputKey:[t?t.outputKey:null,[s.required]]})}}t("GetSumConfigComponent",I),I.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:I,deps:[{token:l.Store},{token:m.FormBuilder}],target:e.ɵɵFactoryTarget.Component}),I.ɵcmp=e.ɵɵngDeclareComponent({minVersion:"12.0.0",version:"12.2.15",type:I,selector:"tb-transformation-node-sum-config",usesInheritance:!0,ngImport:e,template:'<section [formGroup]="getSumConfigForm" fxLayout="column">\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.input-key</mat-label>\n        <input matInput formControlName="inputKey" required>\n    </mat-form-field>\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.output-key</mat-label>\n        <input matInput formControlName="outputKey" required>\n    </mat-form-field>\n</section>\n',components:[{type:i.MatFormField,selector:"mat-form-field",inputs:["color","floatLabel","appearance","hideRequiredMarker","hintLabel"],exportAs:["matFormField"]}],directives:[{type:u.DefaultLayoutDirective,selector:"  [fxLayout], [fxLayout.xs], [fxLayout.sm], [fxLayout.md],  [fxLayout.lg], [fxLayout.xl], [fxLayout.lt-sm], [fxLayout.lt-md],  [fxLayout.lt-lg], [fxLayout.lt-xl], [fxLayout.gt-xs], [fxLayout.gt-sm],  [fxLayout.gt-md], [fxLayout.gt-lg]",inputs:["fxLayout","fxLayout.xs","fxLayout.sm","fxLayout.md","fxLayout.lg","fxLayout.xl","fxLayout.lt-sm","fxLayout.lt-md","fxLayout.lt-lg","fxLayout.lt-xl","fxLayout.gt-xs","fxLayout.gt-sm","fxLayout.gt-md","fxLayout.gt-lg"]},{type:m.NgControlStatusGroup,selector:"[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]"},{type:m.FormGroupDirective,selector:"[formGroup]",inputs:["formGroup"],outputs:["ngSubmit"],exportAs:["ngForm"]},{type:i.MatLabel,selector:"mat-label"},{type:p.TranslateDirective,selector:"[translate],[ngx-translate]",inputs:["translate","translateParams"]},{type:c.MatInput,selector:"input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]",inputs:["id","disabled","required","type","value","readonly","placeholder","errorStateMatcher","aria-describedby"],exportAs:["matInput"]},{type:m.DefaultValueAccessor,selector:"input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]"},{type:m.NgControlStatus,selector:"[formControlName],[ngModel],[formControl]"},{type:m.FormControlName,selector:"[formControlName]",inputs:["disabled","formControlName","ngModel"],outputs:["ngModelChange"]},{type:m.RequiredValidator,selector:":not([type=checkbox])[required][formControlName],:not([type=checkbox])[required][formControl],:not([type=checkbox])[required][ngModel]",inputs:["required"]}]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:I,decorators:[{type:o,args:[{selector:"tb-transformation-node-sum-config",templateUrl:"./get-sum-config.component.html",styleUrls:[]}]}],ctorParameters:function(){return[{type:l.Store},{type:m.FormBuilder}]}});class v{}t("CustomNodesConfigTransformModule",v),v.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:v,deps:[],target:e.ɵɵFactoryTarget.NgModule}),v.ɵmod=e.ɵɵngDeclareNgModule({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:v,declarations:[I],imports:[f,n],exports:[I]}),v.ɵinj=e.ɵɵngDeclareInjector({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:v,imports:[[f,n]]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:v,decorators:[{type:r,args:[{declarations:[I],imports:[f,n],exports:[I]}]}]});class k extends a{constructor(t,e){super(t),this.store=t,this.fb=e,this.separatorKeysCodes=[d,g,x]}configForm(){return this.systemCommandConfigForm}onConfigurationSet(t){this.systemCommandConfigForm=this.fb.group({messageNames:[t?t.messageNames:null,[]],command:[t?t.command:null,[s.required]]})}removeMessageName(t){const e=this.systemCommandConfigForm.get("messageNames").value,o=e.indexOf(t);o>=0&&(e.splice(o,1),this.systemCommandConfigForm.get("messageNames").setValue(e,{emitEvent:!0}))}addMessageName(t){const e=t.input;let o=t.value;if((o||"").trim()){o=o.trim();let t=this.systemCommandConfigForm.get("messageNames").value;t&&-1!==t.indexOf(o)||(t||(t=[]),t.push(o),this.systemCommandConfigForm.get("messageNames").setValue(t,{emitEvent:!0}))}e&&(e.value="")}}t("SystemCommandConfigComponent",k),k.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:k,deps:[{token:l.Store},{token:m.FormBuilder}],target:e.ɵɵFactoryTarget.Component}),k.ɵcmp=e.ɵɵngDeclareComponent({minVersion:"12.0.0",version:"12.2.15",type:k,selector:"tb-action-node-system-command-config",usesInheritance:!0,ngImport:e,template:'<section [formGroup]="systemCommandConfigForm" fxLayout="column">\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.data-keys</mat-label>\n        <mat-chip-list #messageNamesChipList>\n            <mat-chip\n              *ngFor="let messageName of systemCommandConfigForm.get(\'messageNames\').value;"\n              (removed)="removeMessageName(messageName)">\n              {{messageName}}\n              <mat-icon matChipRemove>close</mat-icon>\n            </mat-chip>\n            <input matInput type="text" placeholder="{{\'tb.rulenode.data-keys\' | translate}}"\n                   style="max-width: 200px;"\n                   [matChipInputFor]="messageNamesChipList"\n                   [matChipInputSeparatorKeyCodes]="separatorKeysCodes"\n                   (matChipInputTokenEnd)="addMessageName($event)"\n                   [matChipInputAddOnBlur]="true">\n          </mat-chip-list>\n    </mat-form-field>\n    <mat-form-field class="mat-block">\n        <mat-label translate>tb.rulenode.command</mat-label>\n        <input matInput formControlName="command" required>\n    </mat-form-field>\n</section>\n',components:[{type:i.MatFormField,selector:"mat-form-field",inputs:["color","floatLabel","appearance","hideRequiredMarker","hintLabel"],exportAs:["matFormField"]},{type:C.MatChipList,selector:"mat-chip-list",inputs:["aria-orientation","multiple","compareWith","value","required","placeholder","disabled","selectable","tabIndex","errorStateMatcher"],outputs:["change","valueChange"],exportAs:["matChipList"]},{type:L.MatIcon,selector:"mat-icon",inputs:["color","inline","svgIcon","fontSet","fontIcon"],exportAs:["matIcon"]}],directives:[{type:u.DefaultLayoutDirective,selector:"  [fxLayout], [fxLayout.xs], [fxLayout.sm], [fxLayout.md],  [fxLayout.lg], [fxLayout.xl], [fxLayout.lt-sm], [fxLayout.lt-md],  [fxLayout.lt-lg], [fxLayout.lt-xl], [fxLayout.gt-xs], [fxLayout.gt-sm],  [fxLayout.gt-md], [fxLayout.gt-lg]",inputs:["fxLayout","fxLayout.xs","fxLayout.sm","fxLayout.md","fxLayout.lg","fxLayout.xl","fxLayout.lt-sm","fxLayout.lt-md","fxLayout.lt-lg","fxLayout.lt-xl","fxLayout.gt-xs","fxLayout.gt-sm","fxLayout.gt-md","fxLayout.gt-lg"]},{type:m.NgControlStatusGroup,selector:"[formGroupName],[formArrayName],[ngModelGroup],[formGroup],form:not([ngNoForm]),[ngForm]"},{type:m.FormGroupDirective,selector:"[formGroup]",inputs:["formGroup"],outputs:["ngSubmit"],exportAs:["ngForm"]},{type:i.MatLabel,selector:"mat-label"},{type:p.TranslateDirective,selector:"[translate],[ngx-translate]",inputs:["translate","translateParams"]},{type:y.NgForOf,selector:"[ngFor][ngForOf]",inputs:["ngForOf","ngForTrackBy","ngForTemplate"]},{type:C.MatChip,selector:"mat-basic-chip, [mat-basic-chip], mat-chip, [mat-chip]",inputs:["color","disableRipple","tabIndex","selected","value","selectable","disabled","removable"],outputs:["selectionChange","destroyed","removed"],exportAs:["matChip"]},{type:C.MatChipRemove,selector:"[matChipRemove]"},{type:c.MatInput,selector:"input[matInput], textarea[matInput], select[matNativeControl],      input[matNativeControl], textarea[matNativeControl]",inputs:["id","disabled","required","type","value","readonly","placeholder","errorStateMatcher","aria-describedby"],exportAs:["matInput"]},{type:C.MatChipInput,selector:"input[matChipInputFor]",inputs:["matChipInputSeparatorKeyCodes","placeholder","id","matChipInputFor","matChipInputAddOnBlur","disabled"],outputs:["matChipInputTokenEnd"],exportAs:["matChipInput","matChipInputFor"]},{type:m.DefaultValueAccessor,selector:"input:not([type=checkbox])[formControlName],textarea[formControlName],input:not([type=checkbox])[formControl],textarea[formControl],input:not([type=checkbox])[ngModel],textarea[ngModel],[ngDefaultControl]"},{type:m.NgControlStatus,selector:"[formControlName],[ngModel],[formControl]"},{type:m.FormControlName,selector:"[formControlName]",inputs:["disabled","formControlName","ngModel"],outputs:["ngModelChange"]},{type:m.RequiredValidator,selector:":not([type=checkbox])[required][formControlName],:not([type=checkbox])[required][formControl],:not([type=checkbox])[required][ngModel]",inputs:["required"]}],pipes:{translate:p.TranslatePipe}}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:k,decorators:[{type:o,args:[{selector:"tb-action-node-system-command-config",templateUrl:"./system-command-config.component.html",styleUrls:[]}]}],ctorParameters:function(){return[{type:l.Store},{type:m.FormBuilder}]}});class D{}t("CustomNodesConfigActionModule",D),D.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:D,deps:[],target:e.ɵɵFactoryTarget.NgModule}),D.ɵmod=e.ɵɵngDeclareNgModule({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:D,declarations:[k],imports:[f,n],exports:[k]}),D.ɵinj=e.ɵɵngDeclareInjector({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:D,imports:[[f,n]]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:D,decorators:[{type:r,args:[{declarations:[k],imports:[f,n],exports:[k]}]}]});class S{constructor(t){!function(t){t.setTranslation("en_US",{tb:{rulenode:{"msg-key":"Message key","input-key":"Input key","output-key":"Output key","data-keys":"Data keys",command:"Command"}}},!0),t.setTranslation("zh_CN",{tb:{rulenode:{"msg-key":"消息键","input-key":"输入键","output-key":"输出键","data-keys":"数据键",command:"命令"}}},!0)}(t)}}t("CustomNodesConfigModule",S),S.ɵfac=e.ɵɵngDeclareFactory({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:S,deps:[{token:p.TranslateService}],target:e.ɵɵFactoryTarget.NgModule}),S.ɵmod=e.ɵɵngDeclareNgModule({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:S,imports:[f,n,h],exports:[M,N,v,D]}),S.ɵinj=e.ɵɵngDeclareInjector({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:S,imports:[[f,n,h],M,N,v,D]}),e.ɵɵngDeclareClassMetadata({minVersion:"12.0.0",version:"12.2.15",ngImport:e,type:S,decorators:[{type:r,args:[{imports:[f,n,h],exports:[M,N,v,D]}]}],ctorParameters:function(){return[{type:p.TranslateService}]}})}}}));//# sourceMappingURL=custom-nodes-config.js.map