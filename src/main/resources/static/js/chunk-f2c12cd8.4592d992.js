(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["chunk-f2c12cd8"], {
    4697: function (e, t, a) {
    }, "95ba": function (e, t, a) {
        "use strict";
        a("99df")
    }, "99df": function (e, t, a) {
    }, a434: function (e, t, a) {
        "use strict";
        var n = a("23e7"), s = a("23cb"), i = a("a691"), l = a("50c4"), r = a("7b0b"), o = a("65f0"), u = a("8418"),
            h = a("1dde"), c = a("ae40"), m = h("splice"), d = c("splice", {ACCESSORS: !0, 0: 0, 1: 2}), f = Math.max,
            g = Math.min, p = 9007199254740991, b = "Maximum allowed length exceeded";
        n({target: "Array", proto: !0, forced: !m || !d}, {
            splice: function (e, t) {
                var a, n, h, c, m, d, T = r(this), v = l(T.length), x = s(e, v), F = arguments.length;
                if (0 === F ? a = n = 0 : 1 === F ? (a = 0, n = v - x) : (a = F - 2, n = g(f(i(t), 0), v - x)), v + a - n > p) throw TypeError(b);
                for (h = o(T, n), c = 0; c < n; c++) m = x + c, m in T && u(h, c, T[m]);
                if (h.length = n, a < n) {
                    for (c = x; c < v - n; c++) m = c + n, d = c + a, m in T ? T[d] = T[m] : delete T[d];
                    for (c = v; c > v - n + a; c--) delete T[c - 1]
                } else if (a > n) for (c = v - n; c > x; c--) m = c + n - 1, d = c + a - 1, m in T ? T[d] = T[m] : delete T[d];
                for (c = 0; c < a; c++) T[c + x] = arguments[c + 2];
                return T.length = v - n + a, h
            }
        })
    }, a640: function (e, t, a) {
        "use strict";
        var n = a("d039");
        e.exports = function (e, t) {
            var a = [][e];
            return !!a && n((function () {
                a.call(null, t || function () {
                    throw 1
                }, 1)
            }))
        }
    }, b0c0: function (e, t, a) {
        var n = a("83ab"), s = a("9bf2").f, i = Function.prototype, l = i.toString, r = /^\s*function ([^ (]*)/,
            o = "name";
        n && !(o in i) && s(i, o, {
            configurable: !0, get: function () {
                try {
                    return l.call(this).match(r)[1]
                } catch (e) {
                    return ""
                }
            }
        })
    }, c975: function (e, t, a) {
        "use strict";
        var n = a("23e7"), s = a("4d64").indexOf, i = a("a640"), l = a("ae40"), r = [].indexOf,
            o = !!r && 1 / [1].indexOf(1, -0) < 0, u = i("indexOf"), h = l("indexOf", {ACCESSORS: !0, 1: 0});
        n({target: "Array", proto: !0, forced: o || !u || !h}, {
            indexOf: function (e) {
                return o ? r.apply(this, arguments) || 0 : s(this, e, arguments.length > 1 ? arguments[1] : void 0)
            }
        })
    }, d0d4: function (e, t, a) {
        "use strict";
        a.r(t);
        var n = function () {
                var e = this, t = e.$createElement, a = e._self._c || t;
                return a("div", {staticClass: "contain_box"}, [a("div", {staticClass: "ruleForm_box"}, [a("el-form", {
                    directives: [{
                        name: "show",
                        rawName: "v-show",
                        value: !e.isSubmit,
                        expression: "!isSubmit"
                    }],
                    ref: "ruleForm",
                    staticClass: "demo-ruleForm center",
                    attrs: {model: e.ruleForm, "status-icon": "", rules: e.rules}
                }, [a("el-form-item", {
                    staticClass: "input_box",
                    attrs: {label: "请输入cycleLengthThreshold:  (20-100)"}
                }, [a("el-input", {
                    staticClass: "el-input",
                    attrs: {
                        autocomplete: "off",
                        "prefix-icon": "el-icon-caret-right",
                        placeholder: "20-100",
                        size: "small"
                    },
                    model: {
                        value: e.ruleForm.cycleLengthThreshold, callback: function (t) {
                            e.$set(e.ruleForm, "cycleLengthThreshold", t)
                        }, expression: "ruleForm.cycleLengthThreshold"
                    }
                })], 1), a("el-form-item", {
                    staticClass: "input_box",
                    attrs: {label: "请输入dustLengthThreshold:  (8-10)"}
                }, [a("el-input", {
                    staticClass: "el-input",
                    attrs: {autocomplete: "off", placeholder: "8-10", size: "small", "prefix-icon": "el-icon-caret-right"},
                    model: {
                        value: e.ruleForm.dustLengthThreshold, callback: function (t) {
                            e.$set(e.ruleForm, "dustLengthThreshold", t)
                        }, expression: "ruleForm.dustLengthThreshold"
                    }
                })], 1), a("el-form-item", {
                    staticClass: "tag_content",
                    attrs: {label: "请输入物种数:"}
                }), a("TagItem", {
                    staticClass: "tag_contain",
                    attrs: {Data: e.ruleForm.countNum, index: 1},
                    on: {handleClose: e.handleClose, GetData: e.GetData}
                }), a("el-form-item", {
                    staticClass: "tag_content",
                    attrs: {label: "请输入物种名:"}
                }), a("TagItem", {
                    staticClass: "tag_contain",
                    attrs: {Data: e.ruleForm.animalName, index: 0},
                    on: {handleClose: e.handleClose, GetData: e.GetData}
                }), a("el-upload", {
                    staticClass: "up_load_box",
                    attrs: {
                        action: "/upload",
                        limit: 1,
                        "file-list": e.fileList,
                        "on-exceed": e.handleExceed,
                        "on-remove": e.handleRemove,
                        "auto-upload": !0,
                        "before-upload": e.handlePreview,
                        "on-success": e.handleSuccess
                    }
                }, [a("el-button", {staticClass: "uploadButton"}, [e._v("上传文件")])], 1), a("el-form-item", [a("el-button", {
                    staticClass: "submitButton",
                    attrs: {type: "primary"},
                    on: {
                        click: function (t) {
                            return e.uploadParam()
                        }
                    }
                }, [e._v("提交")]), a("el-button", {
                    staticClass: "submitButton",
                    attrs: {type: "primary"},
                    on: {
                        click: function (t) {
                            return e.backToMain()
                        }
                    }
                }, [e._v("返回首页")])], 1)], 1)], 1)])
            }, s = [], i = (a("a434"), a("b0c0"), a("ac1f"), a("1276"), a("bc3a")), l = a.n(i), r = function () {
                var e = this, t = e.$createElement, a = e._self._c || t;
                return a("div", [a("el-row", [e._l(e.TagData, (function (t, n) {
                    return a("el-col", {key: t, attrs: {span: 2.7}}, [a("el-tag", {
                        directives: [{
                            name: "show",
                            rawName: "v-show",
                            value: !e.changeVisible[n],
                            expression: "!changeVisible[i]"
                        }], attrs: {size: "big", closable: ""}, on: {
                            click: function (a) {
                                return e.changeInput(t)
                            }, close: function (a) {
                                return e.handleClose(t)
                            }
                        }
                    }, [e._v(" " + e._s(t) + " ")]), a("el-input", {
                        directives: [{
                            name: "show",
                            rawName: "v-show",
                            value: e.changeVisible[n],
                            expression: "changeVisible[i]"
                        }],
                        ref: "changeTagInput",
                        refInFor: !0,
                        staticClass: "input-new-tag",
                        attrs: {size: "small"},
                        on: {
                            blur: function (t) {
                                return e.handleChangeConfirm(n)
                            }
                        },
                        model: {
                            value: e.changeValue, callback: function (t) {
                                e.changeValue = t
                            }, expression: "changeValue"
                        }
                    })], 1)
                })), e.inputVisible ? a("el-input", {
                    ref: "saveTagInput",
                    staticClass: "input-new-tag",
                    attrs: {size: "small"},
                    on: {blur: e.handleInputConfirm},
                    model: {
                        value: e.inputValue, callback: function (t) {
                            e.inputValue = t
                        }, expression: "inputValue"
                    }
                }) : a("el-button", {
                    staticClass: "button-new-tag",
                    attrs: {size: "small"},
                    on: {click: e.showInput}
                }, [e._v("+ New")])], 2)], 1)
            }, o = [], u = (a("c975"), {
                name: "TagItem", data: function () {
                    return {
                        TagData: this.Data,
                        inputVisible: !1,
                        changeValue: "",
                        inputValue: "",
                        index: this.Index,
                        changeVisible: [!1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1, !1]
                    }
                }, props: ["Data", "Index"], watch: {
                    Data: function (e) {
                        this.TagData = e
                    }, Index: function (e) {
                        this.index = e
                    }
                }, methods: {
                    handleClose: function (e) {
                        var t = this.TagData.indexOf(e);
                        this.TagData.splice(t, 1), this.$emit("handleClose", t, this.index)
                    }, showInput: function () {
                        var e = this;
                        this.inputVisible = !0, this.$nextTick((function () {
                            e.$refs.saveTagInput.$refs.input.focus()
                        }))
                    }, handleInputConfirm: function () {
                        var e = this.inputValue;
                        e && this.TagData.push(e), this.inputVisible = !1, this.inputValue = "", this.$emit("GetData", this.TagData, this.index)
                    }, handleChangeConfirm: function (e) {
                        this.$set(this.changeVisible, e, !1), this.$set(this.TagData, e, this.changeValue), this.changeValue = "", this.$emit("GetData", this.TagData, this.index)
                    }, changeInput: function (e) {
                        var t = this, a = this.TagData.indexOf(e);
                        this.changeValue = e, this.$set(this.changeVisible, a, !0), this.$nextTick((function () {
                            t.$refs.changeTagInput[a].$refs.input.focus()
                        }))
                    }
                }
            }), h = u, c = (a("95ba"), a("2877")), m = Object(c["a"])(h, r, o, !1, null, "058fa42c", null), d = m.exports,
            f = {
                components: {TagItem: d}, name: "submit", mounted: function () {
                    void 0 !== this.$route.params.data && (this.ruleForm.id = this.$route.params.data.id, this.fileList = [{
                        name: this.$route.params.data.oriName,
                        url: ""
                    }])
                }, methods: {
                    handleClose: function (e, t) {
                        1 === t ? this.ruleForm.animalName.splice(e, 1) : 0 === t && this.ruleForm.countNum.splice(e, 1)
                    }, handleRemove: function () {
                        l.a.get("/delete", {params: {id: this.ruleForm.id}})
                    }, handleExceed: function () {
                        this.$message.warning("当前限制选择 1 个文件")
                    }, handleSuccess: function (e) {
                        this.ruleForm.id = e.data
                    }, handlePreview: function (e) {
                        console.log(e.name);
                        var t = e.name.split(".");
                        if ("sequence" !== t[1]) return this.putMessage("请上传sequence文件"), !1
                    }, backToMain: function () {
                        this.$router.push({name: "mainView", params: {}})
                    }, putMessage: function (e) {
                        this.$message({message: e, duration: 3e3, showClose: !0, type: "warning"})
                    }, uploadParam: function () {
                        var e = this;
                        if (0 !== this.ruleForm.id.length) if (0 !== this.ruleForm.dustLengthThreshold.length) if (this.ruleForm.dustLengthThreshold > 10 || this.ruleForm.dustLengthThreshold < 8) this.putMessage("dustLength不正确"); else if (0 !== this.ruleForm.cycleLengthThreshold.length) if (this.ruleForm.cycleLengthThreshold > 100 || this.ruleForm.cycleLengthThreshold < 10) this.putMessage("cycleLengthThreshold不正确"); else if (this.ruleForm.countNum.length === this.ruleForm.animalName.length) {
                            this.isSubmit = !0;
                            for (var t = "", a = "", n = 0; n < this.ruleForm.countNum.length; n++) t += this.ruleForm.countNum[n], n !== this.ruleForm.countNum.length - 1 && (t += "-");
                            for (var s = 0; s < this.ruleForm.animalName.length; s++) a += this.ruleForm.animalName[s], s !== this.ruleForm.animalName.length - 1 && (a += "-");
                            l.a.get("/submit", {
                                params: {
                                    id: this.ruleForm.id,
                                    cycleLengthThreshold: this.ruleForm.cycleLengthThreshold,
                                    dustLengthThreshold: this.ruleForm.dustLengthThreshold,
                                    countNum: t,
                                    animalName: a
                                }
                            }).then((function (n) {
                                2e4 === n.data.code ? (clearTimeout(e.timer), e.timer = setTimeout((function () {
                                    e.$router.push({name: "mainView"})
                                }), 1e3)) : (e.putMessage(n.data.message), e.isSubmit = !1), l.a.get("/calculate", {
                                    params: {
                                        id: e.ruleForm.id,
                                        cycleLengthThreshold: e.ruleForm.cycleLengthThreshold,
                                        dustLengthThreshold: e.ruleForm.dustLengthThreshold,
                                        countNum: t,
                                        animalName: a
                                    }
                                }).then((function () {
                                    e.$bus.emit("Config_forms", "1"), e.isSubmit = !1
                                }))
                            }))
                        } else this.putMessage("输入个数不正确"); else this.putMessage("请输入参数"); else this.putMessage("请输入参数!"); else this.putMessage("请上传文件!")
                    }, GetData: function (e, t) {
                        1 === t ? this.ruleForm.countNum = e : this.ruleForm.animalName = e
                    }
                }, data: function () {
                    return {
                        fileList: [],
                        TagData: [],
                        isSubmit: !1,
                        ruleForm: {
                            cycleLengthThreshold: "20",
                            dustLengthThreshold: "8",
                            countNum: ["22", "11", "7"],
                            animalName: ["人类", "熊猫", "狮子"],
                            id: ""
                        }
                    }
                }
            }, g = f, p = (a("eaab"), Object(c["a"])(g, n, s, !1, null, "78ac43fe", null));
        t["default"] = p.exports
    }, eaab: function (e, t, a) {
        "use strict";
        a("4697")
    }
}]);
//# sourceMappingURL=chunk-f2c12cd8.4592d992.js.map