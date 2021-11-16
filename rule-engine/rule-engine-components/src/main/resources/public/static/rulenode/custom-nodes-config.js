(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports, require('@angular/core'), require('@angular/common'), require('@home/components/public-api'), require('@ngx-translate/core'), require('@shared/public-api'), require('@ngrx/store'), require('@angular/forms'), require('@angular/cdk/keycodes')) :
    typeof define === 'function' && define.amd ? define('custom-nodes-config', ['exports', '@angular/core', '@angular/common', '@home/components/public-api', '@ngx-translate/core', '@shared/public-api', '@ngrx/store', '@angular/forms', '@angular/cdk/keycodes'], factory) :
    (global = global || self, factory(global['custom-nodes-config'] = {}, global.ng.core, global.ng.common, global.publicApi, global['ngx-translate'], global.shared, global['ngrx-store'], global.ng.forms, global.ng.cdk.keycodes));
}(this, (function (exports, core, common, publicApi, core$1, publicApi$1, store, forms, keycodes) { 'use strict';

    /*! *****************************************************************************
    Copyright (c) Microsoft Corporation.

    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
    REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
    AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
    INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
    LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
    OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
    PERFORMANCE OF THIS SOFTWARE.
    ***************************************************************************** */
    /* global Reflect, Promise */

    var extendStatics = function(d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };

    function __extends(d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    }

    var __assign = function() {
        __assign = Object.assign || function __assign(t) {
            for (var s, i = 1, n = arguments.length; i < n; i++) {
                s = arguments[i];
                for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
            }
            return t;
        };
        return __assign.apply(this, arguments);
    };

    function __rest(s, e) {
        var t = {};
        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
            t[p] = s[p];
        if (s != null && typeof Object.getOwnPropertySymbols === "function")
            for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
                if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                    t[p[i]] = s[p[i]];
            }
        return t;
    }

    function __decorate(decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    }

    function __param(paramIndex, decorator) {
        return function (target, key) { decorator(target, key, paramIndex); }
    }

    function __metadata(metadataKey, metadataValue) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
    }

    function __awaiter(thisArg, _arguments, P, generator) {
        function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
        return new (P || (P = Promise))(function (resolve, reject) {
            function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
            function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
            function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
            step((generator = generator.apply(thisArg, _arguments || [])).next());
        });
    }

    function __generator(thisArg, body) {
        var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
        return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
        function verb(n) { return function (v) { return step([n, v]); }; }
        function step(op) {
            if (f) throw new TypeError("Generator is already executing.");
            while (_) try {
                if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
                if (y = 0, t) op = [op[0] & 2, t.value];
                switch (op[0]) {
                    case 0: case 1: t = op; break;
                    case 4: _.label++; return { value: op[1], done: false };
                    case 5: _.label++; y = op[1]; op = [0]; continue;
                    case 7: op = _.ops.pop(); _.trys.pop(); continue;
                    default:
                        if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                        if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                        if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                        if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                        if (t[2]) _.ops.pop();
                        _.trys.pop(); continue;
                }
                op = body.call(thisArg, _);
            } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
            if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
        }
    }

    var __createBinding = Object.create ? (function(o, m, k, k2) {
        if (k2 === undefined) k2 = k;
        Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
    }) : (function(o, m, k, k2) {
        if (k2 === undefined) k2 = k;
        o[k2] = m[k];
    });

    function __exportStar(m, o) {
        for (var p in m) if (p !== "default" && !Object.prototype.hasOwnProperty.call(o, p)) __createBinding(o, m, p);
    }

    function __values(o) {
        var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
        if (m) return m.call(o);
        if (o && typeof o.length === "number") return {
            next: function () {
                if (o && i >= o.length) o = void 0;
                return { value: o && o[i++], done: !o };
            }
        };
        throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
    }

    function __read(o, n) {
        var m = typeof Symbol === "function" && o[Symbol.iterator];
        if (!m) return o;
        var i = m.call(o), r, ar = [], e;
        try {
            while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
        }
        catch (error) { e = { error: error }; }
        finally {
            try {
                if (r && !r.done && (m = i["return"])) m.call(i);
            }
            finally { if (e) throw e.error; }
        }
        return ar;
    }

    /** @deprecated */
    function __spread() {
        for (var ar = [], i = 0; i < arguments.length; i++)
            ar = ar.concat(__read(arguments[i]));
        return ar;
    }

    /** @deprecated */
    function __spreadArrays() {
        for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
        for (var r = Array(s), k = 0, i = 0; i < il; i++)
            for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
                r[k] = a[j];
        return r;
    }

    function __spreadArray(to, from) {
        for (var i = 0, il = from.length, j = to.length; i < il; i++, j++)
            to[j] = from[i];
        return to;
    }

    function __await(v) {
        return this instanceof __await ? (this.v = v, this) : new __await(v);
    }

    function __asyncGenerator(thisArg, _arguments, generator) {
        if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
        var g = generator.apply(thisArg, _arguments || []), i, q = [];
        return i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i;
        function verb(n) { if (g[n]) i[n] = function (v) { return new Promise(function (a, b) { q.push([n, v, a, b]) > 1 || resume(n, v); }); }; }
        function resume(n, v) { try { step(g[n](v)); } catch (e) { settle(q[0][3], e); } }
        function step(r) { r.value instanceof __await ? Promise.resolve(r.value.v).then(fulfill, reject) : settle(q[0][2], r); }
        function fulfill(value) { resume("next", value); }
        function reject(value) { resume("throw", value); }
        function settle(f, v) { if (f(v), q.shift(), q.length) resume(q[0][0], q[0][1]); }
    }

    function __asyncDelegator(o) {
        var i, p;
        return i = {}, verb("next"), verb("throw", function (e) { throw e; }), verb("return"), i[Symbol.iterator] = function () { return this; }, i;
        function verb(n, f) { i[n] = o[n] ? function (v) { return (p = !p) ? { value: __await(o[n](v)), done: n === "return" } : f ? f(v) : v; } : f; }
    }

    function __asyncValues(o) {
        if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
        var m = o[Symbol.asyncIterator], i;
        return m ? m.call(o) : (o = typeof __values === "function" ? __values(o) : o[Symbol.iterator](), i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i);
        function verb(n) { i[n] = o[n] && function (v) { return new Promise(function (resolve, reject) { v = o[n](v), settle(resolve, reject, v.done, v.value); }); }; }
        function settle(resolve, reject, d, v) { Promise.resolve(v).then(function(v) { resolve({ value: v, done: d }); }, reject); }
    }

    function __makeTemplateObject(cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };

    var __setModuleDefault = Object.create ? (function(o, v) {
        Object.defineProperty(o, "default", { enumerable: true, value: v });
    }) : function(o, v) {
        o["default"] = v;
    };

    function __importStar(mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
        __setModuleDefault(result, mod);
        return result;
    }

    function __importDefault(mod) {
        return (mod && mod.__esModule) ? mod : { default: mod };
    }

    function __classPrivateFieldGet(receiver, state, kind, f) {
        if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
        if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
        return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
    }

    function __classPrivateFieldSet(receiver, state, value, kind, f) {
        if (kind === "m") throw new TypeError("Private method is not writable");
        if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
        if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
        return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
    }

    function addCustomNodesLocaleEnglish(translate) {
        var enUS = {
            tb: {
                rulenode: {
                    'msg-key': 'Message key',
                    'input-key': 'Input key',
                    'output-key': 'Output key',
                    "data-keys": 'Data keys',
                    "command": 'Command'
                }
            }
        };
        var zhCN = {
            tb: {
                rulenode: {
                    'msg-key': '消息键',
                    'input-key': '输入键',
                    'output-key': '输出键',
                    "data-keys": '数据键',
                    "command": '命令'
                }
            }
        };
        translate.setTranslation('en_US', enUS, true);
        translate.setTranslation('zh_CN', zhCN, true);
    }

    var CheckKeyConfigComponent = /** @class */ (function (_super) {
        __extends(CheckKeyConfigComponent, _super);
        function CheckKeyConfigComponent(store, fb) {
            var _this = _super.call(this, store) || this;
            _this.store = store;
            _this.fb = fb;
            return _this;
        }
        CheckKeyConfigComponent.prototype.configForm = function () {
            return this.checkKeyConfigForm;
        };
        CheckKeyConfigComponent.prototype.onConfigurationSet = function (configuration) {
            this.checkKeyConfigForm = this.fb.group({
                key: [configuration ? configuration.key : null, [forms.Validators.required]]
            });
        };
        CheckKeyConfigComponent.ctorParameters = function () { return [
            { type: store.Store },
            { type: forms.FormBuilder }
        ]; };
        CheckKeyConfigComponent = __decorate([
            core.Component({
                selector: 'tb-filter-node-check-key-config',
                template: "<section [formGroup]=\"checkKeyConfigForm\" fxLayout=\"column\">\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.msg-key</mat-label>\n        <input matInput formControlName=\"key\" required>\n    </mat-form-field>\n</section>\n"
            }),
            __metadata("design:paramtypes", [store.Store,
                forms.FormBuilder])
        ], CheckKeyConfigComponent);
        return CheckKeyConfigComponent;
    }(publicApi$1.RuleNodeConfigurationComponent));

    var CustomNodesConfigFilterModule = /** @class */ (function () {
        function CustomNodesConfigFilterModule() {
        }
        CustomNodesConfigFilterModule = __decorate([
            core.NgModule({
                declarations: [
                    CheckKeyConfigComponent
                ],
                imports: [
                    common.CommonModule,
                    publicApi$1.SharedModule
                ],
                exports: [
                    CheckKeyConfigComponent
                ]
            })
        ], CustomNodesConfigFilterModule);
        return CustomNodesConfigFilterModule;
    }());

    var GetSumIntoMetadataConfigComponent = /** @class */ (function (_super) {
        __extends(GetSumIntoMetadataConfigComponent, _super);
        function GetSumIntoMetadataConfigComponent(store, fb) {
            var _this = _super.call(this, store) || this;
            _this.store = store;
            _this.fb = fb;
            return _this;
        }
        GetSumIntoMetadataConfigComponent.prototype.configForm = function () {
            return this.getSumIntoMetadataConfigForm;
        };
        GetSumIntoMetadataConfigComponent.prototype.onConfigurationSet = function (configuration) {
            this.getSumIntoMetadataConfigForm = this.fb.group({
                inputKey: [configuration ? configuration.inputKey : null, [forms.Validators.required]],
                outputKey: [configuration ? configuration.outputKey : null, [forms.Validators.required]]
            });
        };
        GetSumIntoMetadataConfigComponent.ctorParameters = function () { return [
            { type: store.Store },
            { type: forms.FormBuilder }
        ]; };
        GetSumIntoMetadataConfigComponent = __decorate([
            core.Component({
                selector: 'tb-enrichment-node-sum-into-metadata-config',
                template: "<section [formGroup]=\"getSumIntoMetadataConfigForm\" fxLayout=\"column\">\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.input-key</mat-label>\n        <input matInput formControlName=\"inputKey\" required>\n    </mat-form-field>\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.output-key</mat-label>\n        <input matInput formControlName=\"outputKey\" required>\n    </mat-form-field>\n</section>\n"
            }),
            __metadata("design:paramtypes", [store.Store,
                forms.FormBuilder])
        ], GetSumIntoMetadataConfigComponent);
        return GetSumIntoMetadataConfigComponent;
    }(publicApi$1.RuleNodeConfigurationComponent));

    var CustomNodesConfigEnrichmentModule = /** @class */ (function () {
        function CustomNodesConfigEnrichmentModule() {
        }
        CustomNodesConfigEnrichmentModule = __decorate([
            core.NgModule({
                declarations: [
                    GetSumIntoMetadataConfigComponent
                ],
                imports: [
                    common.CommonModule,
                    publicApi$1.SharedModule
                ],
                exports: [
                    GetSumIntoMetadataConfigComponent
                ]
            })
        ], CustomNodesConfigEnrichmentModule);
        return CustomNodesConfigEnrichmentModule;
    }());

    var GetSumConfigComponent = /** @class */ (function (_super) {
        __extends(GetSumConfigComponent, _super);
        function GetSumConfigComponent(store, fb) {
            var _this = _super.call(this, store) || this;
            _this.store = store;
            _this.fb = fb;
            return _this;
        }
        GetSumConfigComponent.prototype.configForm = function () {
            return this.getSumConfigForm;
        };
        GetSumConfigComponent.prototype.onConfigurationSet = function (configuration) {
            this.getSumConfigForm = this.fb.group({
                inputKey: [configuration ? configuration.inputKey : null, [forms.Validators.required]],
                outputKey: [configuration ? configuration.outputKey : null, [forms.Validators.required]]
            });
        };
        GetSumConfigComponent.ctorParameters = function () { return [
            { type: store.Store },
            { type: forms.FormBuilder }
        ]; };
        GetSumConfigComponent = __decorate([
            core.Component({
                selector: 'tb-transformation-node-sum-config',
                template: "<section [formGroup]=\"getSumConfigForm\" fxLayout=\"column\">\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.input-key</mat-label>\n        <input matInput formControlName=\"inputKey\" required>\n    </mat-form-field>\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.output-key</mat-label>\n        <input matInput formControlName=\"outputKey\" required>\n    </mat-form-field>\n</section>\n"
            }),
            __metadata("design:paramtypes", [store.Store,
                forms.FormBuilder])
        ], GetSumConfigComponent);
        return GetSumConfigComponent;
    }(publicApi$1.RuleNodeConfigurationComponent));

    var CustomNodesConfigTransformModule = /** @class */ (function () {
        function CustomNodesConfigTransformModule() {
        }
        CustomNodesConfigTransformModule = __decorate([
            core.NgModule({
                declarations: [
                    GetSumConfigComponent
                ],
                imports: [
                    common.CommonModule,
                    publicApi$1.SharedModule
                ],
                exports: [
                    GetSumConfigComponent
                ]
            })
        ], CustomNodesConfigTransformModule);
        return CustomNodesConfigTransformModule;
    }());

    var SystemCommandConfigComponent = /** @class */ (function (_super) {
        __extends(SystemCommandConfigComponent, _super);
        function SystemCommandConfigComponent(store, fb) {
            var _this = _super.call(this, store) || this;
            _this.store = store;
            _this.fb = fb;
            _this.separatorKeysCodes = [keycodes.ENTER, keycodes.COMMA, keycodes.SEMICOLON];
            return _this;
        }
        SystemCommandConfigComponent.prototype.configForm = function () {
            return this.systemCommandConfigForm;
        };
        SystemCommandConfigComponent.prototype.onConfigurationSet = function (configuration) {
            this.systemCommandConfigForm = this.fb.group({
                messageNames: [configuration ? configuration.messageNames : null, []],
                command: [configuration ? configuration.command : null, [forms.Validators.required]]
            });
        };
        SystemCommandConfigComponent.prototype.removeMessageName = function (messageName) {
            var messageNames = this.systemCommandConfigForm.get('messageNames').value;
            var index = messageNames.indexOf(messageName);
            if (index >= 0) {
                messageNames.splice(index, 1);
                this.systemCommandConfigForm.get('messageNames').setValue(messageNames, { emitEvent: true });
            }
        };
        SystemCommandConfigComponent.prototype.addMessageName = function (event) {
            var input = event.input;
            var value = event.value;
            if ((value || '').trim()) {
                value = value.trim();
                var messageNames = this.systemCommandConfigForm.get('messageNames').value;
                if (!messageNames || messageNames.indexOf(value) === -1) {
                    if (!messageNames) {
                        messageNames = [];
                    }
                    messageNames.push(value);
                    this.systemCommandConfigForm.get('messageNames').setValue(messageNames, { emitEvent: true });
                }
            }
            if (input) {
                input.value = '';
            }
        };
        SystemCommandConfigComponent.ctorParameters = function () { return [
            { type: store.Store },
            { type: forms.FormBuilder }
        ]; };
        SystemCommandConfigComponent = __decorate([
            core.Component({
                selector: 'tb-action-node-system-command-config',
                template: "<section [formGroup]=\"systemCommandConfigForm\" fxLayout=\"column\">\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.data-keys</mat-label>\n        <mat-chip-list #messageNamesChipList>\n            <mat-chip\n              *ngFor=\"let messageName of systemCommandConfigForm.get('messageNames').value;\"\n              (removed)=\"removeMessageName(messageName)\">\n              {{messageName}}\n              <mat-icon matChipRemove>close</mat-icon>\n            </mat-chip>\n            <input matInput type=\"text\" placeholder=\"{{'tb.rulenode.data-keys' | translate}}\"\n                   style=\"max-width: 200px;\"\n                   [matChipInputFor]=\"messageNamesChipList\"\n                   [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\"\n                   (matChipInputTokenEnd)=\"addMessageName($event)\"\n                   [matChipInputAddOnBlur]=\"true\">\n          </mat-chip-list>\n    </mat-form-field>\n    <mat-form-field class=\"mat-block\">\n        <mat-label translate>tb.rulenode.command</mat-label>\n        <input matInput formControlName=\"command\" required>\n    </mat-form-field>\n</section>\n"
            }),
            __metadata("design:paramtypes", [store.Store,
                forms.FormBuilder])
        ], SystemCommandConfigComponent);
        return SystemCommandConfigComponent;
    }(publicApi$1.RuleNodeConfigurationComponent));

    var CustomNodesConfigActionModule = /** @class */ (function () {
        function CustomNodesConfigActionModule() {
        }
        CustomNodesConfigActionModule = __decorate([
            core.NgModule({
                declarations: [
                    SystemCommandConfigComponent
                ],
                imports: [
                    common.CommonModule,
                    publicApi$1.SharedModule
                ],
                exports: [
                    SystemCommandConfigComponent
                ]
            })
        ], CustomNodesConfigActionModule);
        return CustomNodesConfigActionModule;
    }());

    var CustomNodesConfigModule = /** @class */ (function () {
        function CustomNodesConfigModule(translate) {
            addCustomNodesLocaleEnglish(translate);
        }
        CustomNodesConfigModule.ctorParameters = function () { return [
            { type: core$1.TranslateService }
        ]; };
        CustomNodesConfigModule = __decorate([
            core.NgModule({
                imports: [
                    common.CommonModule,
                    publicApi$1.SharedModule,
                    publicApi.HomeComponentsModule
                ],
                exports: [
                    CustomNodesConfigFilterModule,
                    CustomNodesConfigEnrichmentModule,
                    CustomNodesConfigTransformModule,
                    CustomNodesConfigActionModule
                ]
            }),
            __metadata("design:paramtypes", [core$1.TranslateService])
        ], CustomNodesConfigModule);
        return CustomNodesConfigModule;
    }());

    exports.CustomNodesConfigModule = CustomNodesConfigModule;
    exports.ɵa = CustomNodesConfigFilterModule;
    exports.ɵb = CheckKeyConfigComponent;
    exports.ɵc = CustomNodesConfigEnrichmentModule;
    exports.ɵd = GetSumIntoMetadataConfigComponent;
    exports.ɵe = CustomNodesConfigTransformModule;
    exports.ɵf = GetSumConfigComponent;
    exports.ɵg = CustomNodesConfigActionModule;
    exports.ɵh = SystemCommandConfigComponent;

    Object.defineProperty(exports, '__esModule', { value: true });

})));
//# sourceMappingURL=custom-nodes-config.umd.js.map
