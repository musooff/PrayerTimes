package com.simurgh.prayertimes.model

import android.app.Activity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable




open class RxBaseActivity: Activity() {
    private var compositeDisposable = CompositeDisposable()

    public override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    protected fun getCompositeDisposable(): CompositeDisposable {
        return this.compositeDisposable
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}
